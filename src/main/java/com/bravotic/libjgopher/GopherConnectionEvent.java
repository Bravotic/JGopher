/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bravotic.libjgopher;

/**
 *
 * @author collin
 */
public abstract class GopherConnectionEvent {
    public abstract void connecting();
    public abstract void rendering();
    public abstract void finished();
}
