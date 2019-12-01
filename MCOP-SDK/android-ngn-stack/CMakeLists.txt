# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

if (${ANDROID_ABI} STREQUAL "x86")
	set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--no-warn-shared-textrel")
endif ()


SET( DIRECTOR_PROJECT  "${PROJECT_SOURCE_DIR}/../doubango")

SET(libsExtern
		libaudio_processing
		libcommon
		libcommon_audio
		libcrypto
		libdecoder
		libencoder
		libg729b
		libgsm
		libiLBC
		libopencore-amrnb
		libopencore-amrwb
		libvo-amrwbenc
		libopenh264
		libopus
		libprocessing
		libspeex
		libspeexdsp
		libsrtp
		libssl
		libsystem_wrappers
		libvpx
		libx264
		libxml2
		libyuv
		)

SET(libsExternGpl
		libavcodec
		libavdevice
		libavfilter
		libavformat
		libavutil
		libpostproc
		libswresample
		libswscale
		)

#for debug
if (${CMAKE_BUILD_TYPE} STREQUAL "Debug")
	MESSAGE(STATUS "CMAKE_BUILD_TYPE DEBUG")
	set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O0 -g3 -DDEBUG")
	SET( CMAKE_CXX_FLAGS  "${CMAKE_C_FLAGS}")
	set(DEBUG_LEVEL_DATA "DEBUG_LEVEL=DEBUG_LEVEL_INFO")
	set(DEBUG_LEVEL_DATA2 "_DEBUG")
else()
	set(DEBUG_LEVEL_DATA "DEBUG_LEVEL=DEBUG_LEVEL_ERROR")
	set(DEBUG_LEVEL_DATA2 "_RELEASE")
endif ()



# Compiler-specific C++11 activation.
if ("${CMAKE_CXX_COMPILER_ID}" MATCHES "GNU")
	execute_process(
			COMMAND ${CMAKE_CXX_COMPILER} -dumpversion OUTPUT_VARIABLE GCC_VERSION)
	MESSAGE(STATUS "GCC_VERSION VERSION_GREATER 4.7")
	if (NOT (GCC_VERSION VERSION_GREATER 4.7 OR GCC_VERSION VERSION_EQUAL 4.7))
		message(FATAL_ERROR "${PROJECT_NAME} requires g++ 4.7 or greater.")
	endif ()
elseif ("${CMAKE_CXX_COMPILER_ID}" MATCHES "Clang")
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++")
	MESSAGE(STATUS "libc++")
else ()
	message(FATAL_ERROR "Your C++ compiler does not support C++11.")
endif ()





SET(haveLibrerysExtern

		${DEBUG_LEVEL_DATA}
		${DEBUG_LEVEL_DATA2}
		#HAVE_FFMPEG HAVE_LIBSWSCALE_SWSCALE_H HAVE_LIBAVCODEC_AVCODEC_H
		HAVE_OPENH264
		#HAVE_H264_PASSTHROUGH
		HAVE_H264
		#HAVE_LIBVPX # HAVE_LIBVPX for VP8 codec
		#HAVE_OPUS_OPUS_H HAVE_LIBOPUS
		HAVE_OPENCORE_AMR
		HAVE_SPEEX_DENOISE HAVE_SPEEX_DSP HAVE_SPEEX_JB
		HAVE_LIBGSM
		#HAVE_G729
		#HAVE_ILBC
		HAVE_LIBYUV
		#HAVE_LIBJPEG
		HAVE_LIBXML2
		#HAVE_WEBRTC_DENOISE HAVE_WEBRTC HAVE_WEBRTC_NOISE_SUPPRESSION_H HAVE_WEBRTC_NOISE_SUPPRESSION_X_H
		#HAVE_OPENSSL_SSL_H HAVE_OPENSSL_DTLS_SRTP HAVE_OPENSSL_DTLS HAVE_OPENSSL
		HAVE_SRTP_SRTP_H HAVE_SRTP
		#HAVE_STRUCT_RT_METRICS HAVE_STRUCT_RLIMIT HAVE_STRUCT_DIRENT
		#HAVE_BV16
		#HAVE_SOUND_H
		#HAVE_WAVE_API
		)

SET(tinys
		WRAP

		BFCP
		DAV
		HTTP
		IPSEC
		MEDIA
		MSRP
		NET
		RTP
		SAK
		SDP
		SIGCOMP
		SIP
		SMS
		XCAP
		MCPTT
		)

FOREACH(libExtern ${libsExtern})
	MESSAGE(STATUS "add library: ${libExtern}")
	add_library(${libExtern} STATIC IMPORTED)
	set_target_properties(${libExtern} PROPERTIES IMPORTED_LOCATION ${DIRECTOR_PROJECT}/thirdparties/android/${ANDROID_ABI}/lib/dist/${libExtern}.a)
ENDFOREACH(libExtern)

FOREACH(libExternGpl ${libsExternGpl})
	MESSAGE(STATUS "add library: ${libExternGpl}")
	add_library(${libExternGpl} STATIC IMPORTED)
	set_target_properties(${libExternGpl} PROPERTIES IMPORTED_LOCATION ${DIRECTOR_PROJECT}/thirdparties/android/${ANDROID_ABI}/lib/dist/gpl/${libExternGpl}.a)
ENDFOREACH(libExternGpl)

include_directories(${DIRECTOR_PROJECT}/tinySAK/src)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/dhcp)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/dhcp6)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/dns)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/ice)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/mbms)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/stun)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/tls)
include_directories(${DIRECTOR_PROJECT}/tinyNET/src/turn)
file(GLOB sak_src ${DIRECTOR_PROJECT}/tinySAK/src/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyNET/src)
file(GLOB net_src ${DIRECTOR_PROJECT}/tinyNET/src/*.c ${DIRECTOR_PROJECT}/tinyNET/src/*/*.c)

#INCLUDE tinyWRAP_wrap.cxx in code c++
#file(GLOB tinywrap_scr ${PROJECT_SOURCE_DIR}/src/main/java/org/doubango/tinyWRAP/*.cxx)


#For plugins opensles
file(GLOB opensles_src ${DIRECTOR_PROJECT}/plugins/audio_opensles/*.c ${DIRECTOR_PROJECT}/plugins/audio_opensles/*.cxx)
include_directories(${DIRECTOR_PROJECT}/plugins/audio_opensles/audio_opensles)

include_directories(${DIRECTOR_PROJECT}/tinyHTTP/include)
include_directories(${DIRECTOR_PROJECT}/tinyHTTP/include/tinyhttp)
include_directories(${DIRECTOR_PROJECT}/tinyHTTP/include/tinyhttp/auth)
include_directories(${DIRECTOR_PROJECT}/tinyHTTP/include/tinyhttp/headers)
include_directories(${DIRECTOR_PROJECT}/tinyHTTP/include/tinyhttp/parsers)
file(GLOB http_src ${DIRECTOR_PROJECT}/tinyHTTP/src/*.c ${DIRECTOR_PROJECT}/tinyHTTP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinySIP/include)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/api)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/authentication)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/dialogs)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/headers)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/parsers)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/sigcomp)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/transactions)
include_directories(${DIRECTOR_PROJECT}/tinySIP/include/tinysip/transports)
file(GLOB sip_src ${DIRECTOR_PROJECT}/tinySIP/src/*.c ${DIRECTOR_PROJECT}/tinySIP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyDAV/include)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/alsa)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/coreaudio)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/directsound)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/oss)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/wasapi)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/waveapi)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/bfcp)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/amr)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/bfcp)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/bv)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/dtmf)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/fec)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/g711)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/g722)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/g729)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/gsm)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/h261)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/h263)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/h264)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/ilbc)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/mp4ves)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/msrp)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/opus)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/speex)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/t140)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/theora)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/vpx)

include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/mcptt)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/msrp)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/t140)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/gdi)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/jb)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/mf)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/v4linux)
include_directories(${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/winm)
file(GLOB dav_src ${DIRECTOR_PROJECT}/tinyDAV/src/*.c ${DIRECTOR_PROJECT}/tinyDAV/src/*/*.c ${DIRECTOR_PROJECT}/tinyDAV/src/*/*/*.c ${DIRECTOR_PROJECT}/tinyDAV/src/*.cxx ${DIRECTOR_PROJECT}/tinyDAV/src/*/*.cxx ${DIRECTOR_PROJECT}/tinyDAV/src/*/*/*.cxx)

include_directories(${DIRECTOR_PROJECT}/tinySDP/include)
include_directories(${DIRECTOR_PROJECT}/tinySDP/include/tinysdp)
include_directories(${DIRECTOR_PROJECT}/tinySDP/include/tinysdp/headers)
include_directories(${DIRECTOR_PROJECT}/tinySDP/include/tinysdp/parsers)
file(GLOB sdp_src ${DIRECTOR_PROJECT}/tinySDP/src/*.c ${DIRECTOR_PROJECT}/tinySDP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyMEDIA/include)
include_directories(${DIRECTOR_PROJECT}/tinyMEDIA/include/tinymedia)
include_directories(${DIRECTOR_PROJECT}/tinyMEDIA/include/tinymedia/content)
file(GLOB media_src ${DIRECTOR_PROJECT}/tinyMEDIA/src/*.c ${DIRECTOR_PROJECT}/tinyMEDIA/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyXCAP/include)
include_directories(${DIRECTOR_PROJECT}/tinyXCAP/include/tinyxcap)
file(GLOB xcap_src ${DIRECTOR_PROJECT}/tinyXCAP/src/*.c ${DIRECTOR_PROJECT}/tinyXCAP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinySMS/include)
include_directories(${DIRECTOR_PROJECT}/tinySMS/include/tinysms)
include_directories(${DIRECTOR_PROJECT}/tinySMS/include/tinysms/rpdu)
include_directories(${DIRECTOR_PROJECT}/tinySMS/include/tinysms/tpdu)
file(GLOB sms_src ${DIRECTOR_PROJECT}/tinySMS/src/*.c ${DIRECTOR_PROJECT}/tinySMS/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyMSRP/include)
include_directories(${DIRECTOR_PROJECT}/tinyMSRP/include/tinymsrp)
include_directories(${DIRECTOR_PROJECT}/tinyMSRP/include/tinymsrp/headers)
include_directories(${DIRECTOR_PROJECT}/tinyMSRP/include/tinymsrp/parsers)
include_directories(${DIRECTOR_PROJECT}/tinyMSRP/include/tinymsrp/session)
file(GLOB msrp_src ${DIRECTOR_PROJECT}/tinyMSRP/src/*.c ${DIRECTOR_PROJECT}/tinyMSRP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyRTP/include)
include_directories(${DIRECTOR_PROJECT}/tinyRTP/include/tinyrtp)
include_directories(${DIRECTOR_PROJECT}/tinyRTP/include/tinyrtp/rtcp)
include_directories(${DIRECTOR_PROJECT}/tinyRTP/include/tinyrtp/rtp)
file(GLOB rtp_src ${DIRECTOR_PROJECT}/tinyRTP/src/*.c ${DIRECTOR_PROJECT}/tinyRTP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyBFCP/include)
include_directories(${DIRECTOR_PROJECT}/tinyBFCP/include/tinybfcp)
file(GLOB bfcp_src ${DIRECTOR_PROJECT}/tinyBFCP/src/*.c ${DIRECTOR_PROJECT}/tinyBFCP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyIPSec/src)
file(GLOB ipsec_src ${DIRECTOR_PROJECT}/tinyIPSec/src/*.c ${DIRECTOR_PROJECT}/tinyIPSec/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinySIGCOMP/src)
file(GLOB sigcomp_src ${DIRECTOR_PROJECT}/tinySIGCOMP/src/*.c ${DIRECTOR_PROJECT}/tinySIGCOMP/src/*/*.c)

include_directories(${DIRECTOR_PROJECT}/tinyMCPTT/include)
include_directories(${DIRECTOR_PROJECT}/tinyMCPTT/include/tinymcptt)
include_directories(${DIRECTOR_PROJECT}/tinyMCPTT/include/tinymcptt/packet)
file(GLOB mcptt_src ${DIRECTOR_PROJECT}/tinyMCPTT/src/*.c ${DIRECTOR_PROJECT}/tinyMCPTT/src/*/*.c)


include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libfreetype)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libfreetype/freetype2)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libfreetype/freetype2/freetype)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libfreetype/freetype2/freetype/config)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libxml)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libxml2)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/libyuv)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/ogg)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/opus)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/speex)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/theora)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/vorbis)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/webrtc)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include/wels)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common/include)
include_directories(${DIRECTOR_PROJECT}/thirdparties/common)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/g729b)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/ilbc)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libavcodec)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libavdevice)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libavfilter)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libavformat)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libavutil)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libpostproc)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libswresample)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libswscale)
#include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libyuv)
#include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/libyuv/libyuv)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/opencore-amrnb)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/opencore-amrwb)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/openssl)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/opus)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/speex)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/srtp)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/vo-amrwbenc)
include_directories(${DIRECTOR_PROJECT}/thirdparties/android/_common/include/vpx)

include_directories(${DIRECTOR_PROJECT}/bindings/java/android)
file(GLOB bindings_common ${DIRECTOR_PROJECT}/bindings/_common/*.cxx)
#file(GLOB bindings_common_java_android ${DIRECTOR_PROJECT}/bindings/_common/java/android/*.cxx)

add_definitions(-DHAVE_LIBXML2)
add_definitions(-DANDROID)


FOREACH(tiny ${tinys})
	MESSAGE(STATUS "have tiny: ${tiny}")
	add_definitions(-DTINY${tiny}_EXPORTS)
ENDFOREACH(tiny)
FOREACH(havelib ${haveLibrerysExtern})
	MESSAGE(STATUS "have librery: ${havelib}")
	add_definitions(-D${havelib})
ENDFOREACH(havelib)

#MESSAGE(STATUS "Include plugin OpenSL ES")

#add_library(
#	plugin_audio_opensles
#	SHARED
#	${opensles_src}
#)

#target_include_directories(
#plugin_audio_opensles
#PUBLIC
#${DIRECTOR_PROJECT}/tinySAK/src
#${DIRECTOR_PROJECT}/tinyDAV/include
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/alsa
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/coreaudio
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/directsound
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/oss
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/wasapi
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/audio/waveapi
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/bfcp
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/amr
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/bfcp
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/bv
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/dtmf
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/fec
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/g711
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/g722
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/g729
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/gsm
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/h261
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/h263
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/h264
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/ilbc
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/mp4ves
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/msrp
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/opus
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/speex
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/t140
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/theora
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/codecs/vpx

#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/mcptt
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/msrp
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/t140
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/gdi
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/jb
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/mf
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/v4linux
#${DIRECTOR_PROJECT}/tinyDAV/include/tinydav/video/winm

#${DIRECTOR_PROJECT}/tinyMEDIA/include
#${DIRECTOR_PROJECT}/tinyMEDIA/include/tinymedia
#${DIRECTOR_PROJECT}/tinyMEDIA/include/tinymedia/content

#${DIRECTOR_PROJECT}/tinySDP/include
#${DIRECTOR_PROJECT}/tinySDP/include/tinysdp
#${DIRECTOR_PROJECT}/tinySDP/include/tinysdp/headers
#${DIRECTOR_PROJECT}/tinySDP/include/tinysdp/parsers

#${DIRECTOR_PROJECT}/tinyRTP/include
#${DIRECTOR_PROJECT}/tinyRTP/include/tinyrtp
#${DIRECTOR_PROJECT}/tinyRTP/include/tinyrtp/rtcp
#${DIRECTOR_PROJECT}/tinyRTP/include/tinyrtp/rtp
#)

MESSAGE(STATUS "Add library plugin OpenSL ES")


add_library(
		tinyWRAP
		SHARED
		${sak_src}
		${net_src}
		#${tinywrap_scr}
		${http_src}
		${sip_src}
		${dav_src}
		${sdp_src}
		${media_src}
		${xcap_src}
		${sms_src}
		${msrp_src}
		${rtp_src}
		${bfcp_src}
		${ipsec_src}
		${sigcomp_src}
		${mcptt_src}
		${bindings_common}
		#${bindings_common_java_android}
		${opensles_src}
)


find_library(log-lib log)
find_library(opensles-lib OpenSLES)

target_include_directories(tinyWRAP PRIVATE
		${DIRECTOR_PROJECT}/thirdparties/_common/include
		)



#target_link_libraries(plugin_audio_opensles
#${log-lib}
#${opensles-lib}
#)



target_link_libraries(tinyWRAP
		${log-lib}
		${opensles-lib}
		libaudio_processing
		libcommon
		libcommon_audio
		libcrypto
		libdecoder
		libencoder
		libg729b
		libgsm
		libiLBC
		libopencore-amrnb
		libopencore-amrwb
		libvo-amrwbenc
		libopenh264
		libopus
		libprocessing
		libspeex
		libspeexdsp
		libsrtp
		libssl
		libsystem_wrappers
		libvpx
		libx264
		libxml2
		libyuv
		#GPL
		libavcodec
		libavdevice
		libavfilter
		libavformat
		libavutil
		libpostproc
		libswresample
		libswscale
		)
