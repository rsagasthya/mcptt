##### CSharp
#echo "--->CSharp...<---"
#swig -c++ -csharp -namespace org.doubango.tinyWRAP -outdir csharp -o csharp/tinyWRAP_wrap.cxx csharp/csharp.i

##### Objective-C
#echo "--->Objective-C...<---"
#swig -c++ -objc -outdir objc -o -objc/tinyWRAP_wrap.cxx -objc/-objc.i


DIR_PROJECT="../..";
OS="`uname`"

##### Java
echo "--->Java...<---"
swig -c++ -java -package org.doubango.tinyWRAP -outdir java -o java/tinyWRAP_wrap.cxx java/java.i
echo "Java(Google Dalvik)..."
echo "Google Android special tasks"
rm -R java/android/*.java
rm -R java/*.java
swig -noexcept -c++ -java -package org.doubango.tinyWRAP -outdir java/android -o java/android/tinyWRAP_wrap.cxx java/java.i

if [[ "$OS" == "Darwin"* ]]; then
	#MAC OSX
	sed -i '' 's/dynamic_cast/static_cast/g' java/android/tinyWRAP_wrap.cxx
	sed -i '' 's/|twrap_media_floor_control/ | twrap_media_floor_control.swigValue/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mcptt|twrap_media_audio/(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_audio_ptt_mcptt|twrap_media_group/(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_location|twrap_media_mcptt/(0x01 << 15)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_affiliation|twrap_media_mcptt/(0x01 << 16)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mbms|twrap_media_mcptt/(0x01 << 17)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_authentication|twrap_media_mcptt/(0x01 << 18)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_emergency|twrap_media_audio_ptt_mcptt/(0x01 << 19)|(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mcptt_emergence|twrap_media_group/(0x01 << 19)|(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_alert|twrap_media_audio_ptt_mcptt/(0x01 << 20)|(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mcptt_alert|twrap_media_group/(0x01 << 20)|(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_imminentperil|twrap_media_audio_ptt_mcptt/(0x01 << 21)|(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mcptt_imminentperil|twrap_media_group/(0x01 << 21)|(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mcptt|twrap_media_cms/twrap_media_mcptt.swigValue|twrap_media_cms.swigValue/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_mcptt|twrap_media_gms/twrap_media_mcptt.swigValue|twrap_media_gms.swigValue/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_audio_ptt_mcptt|twrap_media_mcptt_chat|twrap_media_group/twrap_media_audio_ptt_mcptt.swigValue|twrap_media_mcptt_chat.swigValue|twrap_media_group.swigValue/g' java/android/twrap_media_type_t.java
	sed -i '' 's/twrap_media_audio_ptt_mcptt|twrap_media_mcptt_chat/twrap_media_audio_ptt_mcptt.swigValue|twrap_media_mcptt_chat.swigValue/g' java/android/twrap_media_type_t.java

else
	#NOT MAC OSX
	sed -i 's/dynamic_cast/static_cast/g' java/android/tinyWRAP_wrap.cxx
	sed -i 's/|twrap_media_floor_control/ | twrap_media_floor_control.swigValue/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mcptt|twrap_media_audio/(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_audio_ptt_mcptt|twrap_media_group/(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_location|twrap_media_mcptt/(0x01 << 15)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_affiliation|twrap_media_mcptt/(0x01 << 16)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mbms|twrap_media_mcptt/(0x01 << 17)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_authentication|twrap_media_mcptt/(0x01 << 18)|(0x01 << 13)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_emergency|twrap_media_audio_ptt_mcptt/(0x01 << 19)|(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mcptt_emergence|twrap_media_group/(0x01 << 19)|(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_alert|twrap_media_audio_ptt_mcptt/(0x01 << 20)|(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mcptt_alert|twrap_media_group/(0x01 << 20)|(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_imminentperil|twrap_media_audio_ptt_mcptt/(0x01 << 21)|(0x01 << 13)|(0x01)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mcptt_imminentperil|twrap_media_group/(0x01 << 21)|(0x01 << 13)|(0x01)|(0x01 << 14)/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mcptt|twrap_media_cms/twrap_media_mcptt.swigValue|twrap_media_cms.swigValue/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_mcptt|twrap_media_gms/twrap_media_mcptt.swigValue|twrap_media_gms.swigValue/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_audio_ptt_mcptt|twrap_media_mcptt_chat|twrap_media_group/twrap_media_audio_ptt_mcptt.swigValue|twrap_media_mcptt_chat.swigValue|twrap_media_group.swigValue/g' java/android/twrap_media_type_t.java
	sed -i 's/twrap_media_audio_ptt_mcptt|twrap_media_mcptt_chat/twrap_media_audio_ptt_mcptt.swigValue|twrap_media_mcptt_chat.swigValue/g' java/android/twrap_media_type_t.java

fi

if [[ "$OS" == "Darwin"* ]]; then
	#MAC OSX
	sed -i '' 's///g' java/android/twrap_media_type_t.java
else
	#NOT MAC OSX
	sed -i 's///g' java/android/twrap_media_type_t.java
fi
rm $DIR_PROJECT/android-ngn-stack/src/main/java/org/doubango/tinyWRAP/*
cp java/android/*.java $DIR_PROJECT/android-ngn-stack/src/main/java/org/doubango/tinyWRAP/
mv java/android/*.cxx  _common/
mv java/android/*.h  _common/
if [[ "$OS" == "Darwin"* ]]; then
	#MAC OSX
	sed -i '' 's/director_->swig_jvm_->AttachCurrentThread((void **) &jenv_, NULL);/(director_->swig_jvm_->AttachCurrentThread( &jenv_, NULL);/g' _common/tinyWRAP_wrap.cxx
else
	#NOT MAC OSX
	sed -i 's/director_->swig_jvm_->AttachCurrentThread((void **) &jenv_, NULL);/(director_->swig_jvm_->AttachCurrentThread( &jenv_, NULL);/g' _common/tinyWRAP_wrap.cxx
fi

#sed -i 's/AttachCurrentThread((void \*\*)/AttachCurrentThread((JNIEnv \*\*)/g' java/android/tinyWRAP_wrap.cxx
#sed -i 's/AttachCurrentThreadAsDaemon((void \*\*)/AttachCurrentThreadAsDaemon((JNIEnv \*\*)/g' java/android/tinyWRAP_wrap.cxx
#for f in SipCallback DDebugCallback ProxyPluginMgrCallback ProxyAudioConsumerCallback ProxyAudioProducerCallback ProxyVideoProducerCallback ProxyVideoConsumerCallback XcapCallback  MsrpCallback T140Callback
#do
#	sed -i 's/_director_connect(this, swigCPtr, swigCMemOwn, true)/_director_connect(this, swigCPtr, swigCMemOwn, false)/g' java/android/$f.java
#done

##### Python
echo "--->Python...<---"
#swig -c++ -python -outdir python -o python/tinyWRAP_wrap.cxx python/python.i

##### Perl
echo "--->Perl...<---"
#swig -c++ -perl -outdir perl -o Perl/tinyWRAP_wrap.cxx perl/perl.i

##### Ruby
echo "--->Ruby...<---"
#swig -c++ -ruby -outdir Ruby -o Ruby/tinyWRAP_wrap.cxx ruby/ruby.i
