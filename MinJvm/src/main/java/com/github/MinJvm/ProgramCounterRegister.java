package com.github.MinJvm;

import com.github.zxh.classpy.classfile.bytecode.Instruction;
import lombok.Data;

import java.util.Stack;

@Data
public class ProgramCounterRegister {
    private Stack<StackFrame> stack;

    public ProgramCounterRegister(Stack<StackFrame> stack) {
        this.stack = stack;
    }

    public Instruction getNextInstruction(){
        if (stack.isEmpty()){
            return null;
        } else {
            StackFrame currentStackFrame = getTopStackFram();
            return currentStackFrame.getNextInstruction();
        }
    }

    public StackFrame getTopStackFram(){
        return stack.peek();
    }
}
