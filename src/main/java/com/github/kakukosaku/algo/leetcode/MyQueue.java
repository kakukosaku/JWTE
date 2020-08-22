package com.github.kakukosaku.algo.leetcode;

import java.util.Stack;

/**
 * LeetCode-232 Implement Queue using Stacks
 *
 * @author kaku
 * Date    2020/8/6
 */
public class MyQueue {

    private Stack<Integer> stackToPop;
    private Stack<Integer> stackToPush;

    /**
     * Initialize your data structure here.
     */
    public MyQueue() {
        this.stackToPush = new Stack<>();
        this.stackToPop = new Stack<>();
    }

    /**
     * Push element x to the back of queue.
     */
    public void push(int x) {
        if (this.stackToPop.empty() && this.stackToPush.empty()) {
            this.stackToPop.push(x);
        } else {
            this.stackToPush.push(x);
        }
    }

    /**
     * Removes the element from in front of queue and returns that element.
     */
    public int pop() {
        if (this.stackToPop.empty()) {
            while (!this.stackToPush.empty()) {
                this.stackToPop.push(this.stackToPush.pop());
            }
        }

        return this.stackToPop.pop();
    }

    /**
     * Get the front element.
     */
    public int peek() {
        if (this.stackToPop.empty()) {
            while (!this.stackToPush.empty()) {
                this.stackToPop.push(this.stackToPush.pop());
            }
        }
        return this.stackToPop.peek();
    }

    /**
     * Returns whether the queue is empty.
     */
    public boolean empty() {
        return this.stackToPop.empty() && this.stackToPush.empty();
    }

}