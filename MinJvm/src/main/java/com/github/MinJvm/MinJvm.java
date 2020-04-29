package com.github.MinJvm;

import com.github.zxh.classpy.classfile.ClassFile;
import com.github.zxh.classpy.classfile.ClassFileParser;
import com.github.zxh.classpy.classfile.MethodInfo;
import com.github.zxh.classpy.classfile.bytecode.Instruction;
import com.github.zxh.classpy.classfile.bytecode.InstructionCp2;
import com.github.zxh.classpy.classfile.constant.ConstantClassInfo;
import com.github.zxh.classpy.classfile.constant.ConstantFieldrefInfo;
import com.github.zxh.classpy.classfile.constant.ConstantNameAndTypeInfo;
import com.github.zxh.classpy.classfile.constant.ConstantPool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;

public class MinJvm {
    private String mainClass;
    private String[] classPaths;

    public MinJvm(String mainClass, String classPaths) {
        this.mainClass = mainClass;
        this.classPaths = classPaths.split(File.pathSeparator);
    }


    public static void main(String[] args) {
        new MinJvm("com.github.MinJvm.SimpleClass", "target/classes/").start();
    }

    public void start() {
        ClassFile classFile = loadClassFileFromClassPath(mainClass);
        MethodInfo mainInfo = classFile.getMethod("main").get(0);
        Object[] localVariableTable = new Object[mainInfo.getMaxStack()];
        localVariableTable[0] = null;
        Stack<Object> operandStack = new Stack();
        StackFrame stackFrame = new StackFrame(localVariableTable, mainInfo, operandStack);
        Stack<StackFrame> stack = new Stack<>();
        stack.push(stackFrame);

        ProgramCounterRegister programCounterRegister = new ProgramCounterRegister(stack);
        while (true) {
            Instruction instruction = programCounterRegister.getNextInstruction();
            if (instruction == null) {
                break;
            }
            switch (instruction.getOpcode()) {
                case getstatic:
                    ConstantPool constantPool = classFile.getConstantPool();
                    int targetFieldIndex = InstructionCp2.class.cast(instruction).getTargetFieldIndex();
                    ConstantFieldrefInfo fieldrefInfo = constantPool.getFieldrefInfo(targetFieldIndex);
                    ConstantClassInfo classInfo = fieldrefInfo.getClassInfo(constantPool);
                    ConstantNameAndTypeInfo fieldNameAndTypeInfo = fieldrefInfo.getFieldNameAndTypeInfo(constantPool);
                    int nameIndex = classInfo.getNameIndex();
                    String className = constantPool.getUtf8String(nameIndex);
                    String fileName = fieldNameAndTypeInfo.getName(constantPool);
                    if ("java/lang/System".equals(className) && "out".equals(fileName)) {
                        Object staticFiled = System.out;
                        programCounterRegister.getTopStackFram().pushObjectToOperandStack(staticFiled);
                    } else {
                        throw new IllegalStateException("className " + className + "not yet implements");
                    }

                    break;
                case invokestatic:
                    break;
                case invokevirtual:
                    break;
                case _return:
                    break;
                case iinc:
                    break;
                default:
                    throw new IllegalStateException("opcode " + instruction.getOpcode() + "not implements");
            }
        }
    }

    private ClassFile loadClassFileFromClassPath(String fqcn) {
        return Stream.of(classPaths).map(entry -> tryLoad(entry, fqcn)).
                filter(Objects::nonNull).findFirst().
                orElseThrow(() -> new RuntimeException(new ClassCastException(fqcn)));
    }

    private ClassFile tryLoad(String classPath, String fqcn) {
        try {
            byte[] bytes = Files.readAllBytes(new File(classPath, fqcn.replace(".", "/").concat(".class")).toPath());
            return new ClassFileParser().parse(bytes);
        } catch (IOException e) {
            return null;
        }
    }
}
