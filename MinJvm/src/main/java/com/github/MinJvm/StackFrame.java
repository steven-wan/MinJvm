package com.github.MinJvm;

import com.github.zxh.classpy.classfile.MethodInfo;
import com.github.zxh.classpy.classfile.bytecode.Instruction;
import lombok.Data;

import java.util.Stack;

@Data
public class StackFrame {
    private Object[] localVariableTable;

    private Stack<Object> operandStack;

    private MethodInfo methodInfo;

    private int currentInstruction;

    public StackFrame(Object[] localVariableTable, MethodInfo methodInfo,Stack<Object> operandStack) {
        this.localVariableTable = localVariableTable;
        this.methodInfo = methodInfo;
        this.operandStack = operandStack;
    }

    public Instruction getNextInstruction() {
        return methodInfo.getCode().get(currentInstruction++);
    }

    public void pushObjectToOperandStack(Object staticFiled) {
        operandStack.push(staticFiled);
    }
}
