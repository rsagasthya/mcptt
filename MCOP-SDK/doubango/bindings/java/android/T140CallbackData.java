/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class T140CallbackData {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected T140CallbackData(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(T140CallbackData obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tinyWRAPJNI.delete_T140CallbackData(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public byte[] getData() {
    final int size = (int)this.getSize();
    if(size > 0){
		final java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(size);
        final int read = (int)this.getData(buffer, size);
        final byte[] bytes = new byte[read];
        buffer.get(bytes, 0, read);
        return bytes;
    }
    return null;
  }

  public tmedia_t140_data_type_t getType() {
    return tmedia_t140_data_type_t.swigToEnum(tinyWRAPJNI.T140CallbackData_getType(swigCPtr, this));
  }

  public long getSize() {
    return tinyWRAPJNI.T140CallbackData_getSize(swigCPtr, this);
  }

  public long getData(java.nio.ByteBuffer pOutput, long nMaxsize) {
    return tinyWRAPJNI.T140CallbackData_getData(swigCPtr, this, pOutput, nMaxsize);
  }

}
