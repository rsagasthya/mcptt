/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class MessagingAffiliationSession extends SipSession {
  private long swigCPtr;

  protected MessagingAffiliationSession(long cPtr, boolean cMemoryOwn) {
    super(tinyWRAPJNI.MessagingAffiliationSession_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MessagingAffiliationSession obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tinyWRAPJNI.delete_MessagingAffiliationSession(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public MessagingAffiliationSession(SipStack pStack) {
    this(tinyWRAPJNI.new_MessagingAffiliationSession(SipStack.getCPtr(pStack), pStack), true);
  }

  public boolean send(java.nio.ByteBuffer payload, long len, ActionConfig config) {
    return tinyWRAPJNI.MessagingAffiliationSession_send__SWIG_0(swigCPtr, this, payload, len, ActionConfig.getCPtr(config), config);
  }

  public boolean send(java.nio.ByteBuffer payload, long len) {
    return tinyWRAPJNI.MessagingAffiliationSession_send__SWIG_1(swigCPtr, this, payload, len);
  }

  public boolean accept(ActionConfig config) {
    return tinyWRAPJNI.MessagingAffiliationSession_accept__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean accept() {
    return tinyWRAPJNI.MessagingAffiliationSession_accept__SWIG_1(swigCPtr, this);
  }

  public boolean reject(ActionConfig config) {
    return tinyWRAPJNI.MessagingAffiliationSession_reject__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean reject() {
    return tinyWRAPJNI.MessagingAffiliationSession_reject__SWIG_1(swigCPtr, this);
  }

}
