/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class SubscriptionGMSEvent extends SipEvent {
  private long swigCPtr;

  protected SubscriptionGMSEvent(long cPtr, boolean cMemoryOwn) {
    super(tinyWRAPJNI.SubscriptionGMSEvent_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SubscriptionGMSEvent obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tinyWRAPJNI.delete_SubscriptionGMSEvent(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public tsip_subscribe_event_type_t getType() {
    return tsip_subscribe_event_type_t.swigToEnum(tinyWRAPJNI.SubscriptionGMSEvent_getType(swigCPtr, this));
  }

  public SubscriptionGMSSession getSession() {
    long cPtr = tinyWRAPJNI.SubscriptionGMSEvent_getSession(swigCPtr, this);
    return (cPtr == 0) ? null : new SubscriptionGMSSession(cPtr, false);
  }

  public SubscriptionGMSSession takeSessionOwnership() {
    long cPtr = tinyWRAPJNI.SubscriptionGMSEvent_takeSessionOwnership(swigCPtr, this);
    return (cPtr == 0) ? null : new SubscriptionGMSSession(cPtr, false);
  }

}