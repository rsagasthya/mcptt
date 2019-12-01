#if HAVE_CRT
#define _CRTDBG_MAP_ALLOC 
#include <stdlib.h> 
#include <crtdbg.h>
#endif //HAVE_CRT
/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
* Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source Doubango Framework
* Copyright (C) 2010-2011 Mamadou Diop.
* Copyright (C) 2012 Doubango Telecom <http://doubango.org>
*
* This file is part of Open Source Doubango Framework.
*
* DOUBANGO is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* DOUBANGO is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with DOUBANGO.
*
*/


/**@file tsip_challenge.c
 * @brief SIP authentication challenge.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include "tinysip/authentication/tsip_challenge.h"

#include "tinysip/headers/tsip_header_Authorization.h"
#include "tinysip/headers/tsip_header_Proxy_Authorization.h"

#include "tsk_string.h"
#include "tsk_debug.h"
#include "tsk_memory.h"
#include "tsk_base64.h"
#include "tsk_hmac.h"

#include <string.h>


#define TSIP_CHALLENGE_IS_DIGEST(self)	((self) ? tsk_striequals((self)->scheme, "Digest") : 0)
#define TSIP_CHALLENGE_IS_AKAv1(self)	((self) ? tsk_striequals((self)->algorithm, "AKAv1-MD5") : 0)
#define TSIP_CHALLENGE_IS_AKAv2(self)	((self) ? tsk_striequals((self)->algorithm, "AKAv2-MD5") : 0)

#define TSIP_CHALLENGE_STACK(self)		(TSIP_STACK((self)->stack))
#define TSIP_CHALLENGE_USERNAME(self)	(self)->username
#define TSIP_CHALLENGE_PASSWORD(self)	TSIP_CHALLENGE_STACK(self)->identity.password


/** Creates new challenge object. */
tsip_challenge_t* tsip_challenge_create(tsip_stack_t* stack, tsk_bool_t isproxy, const char* scheme, const char* realm, const char* nonce, const char* opaque, const char* algorithm,const char* auts,const char* auts_param,const char* auts_value, const char* qop)
{
	return tsk_object_new(tsip_challenge_def_t, stack, isproxy,scheme, realm, nonce, opaque, algorithm,auts,auts_param,auts_value, qop);
}

/** Creates new challenge object (with default values). */
tsip_challenge_t* tsip_challenge_create_null(tsip_stack_t* stack)
{
	return tsip_challenge_create(stack, tsk_false, tsk_null, tsk_null, tsk_null, tsk_null, tsk_null,tsk_null,tsk_null,tsk_null, tsk_null);
}


int tsip_challenge_reset_cnonce(tsip_challenge_t *self)
{
	if(self){
		if(self->qop) /* client nonce is only used if qop=auth, auth-int or both */
		{
#if 0
			memcpy(self->cnonce, "ecb1d3f6931803ce7ae68099cb946594", 32);
#else
			tsk_istr_t istr;
			
			tsk_strrandom(&istr);
			tsk_md5compute(istr, tsk_strlen(istr), &self->cnonce);
#endif
			self->nc = 1;
		}
	}
	return -1;
}

//3GPP TS 35.205/6/7/8/9 and RFC 3310
int tsip_challenge_get_akares(tsip_challenge_t *self, char const *password,uint8_t** auts_bin , char** result)
{
	char *nonce = tsk_null;
	int ret = -1;
    uint8_t* hex_key=tsk_null;
	if(tsk_register_get_cb()){
		TSK_DEBUG_INFO("Use authentication SIM");
		tsk_size_t n;
		AKA_RAND_T rand;
		AKA_AUTN_T autn;
		tsk_buffer_t* resultAka_base64;
		char *commandAuthBase64=tsk_null;
		uint8_t *commandAuth=tsk_null;
		uint8_t *resultAka=tsk_null;

		uint8_t *res=tsk_null;
		uint8_t *ck=tsk_null;
		uint8_t *ik=tsk_null;
		uint8_t *kc=tsk_null;
		int size_ck=-1;
		int size_ik=-1;
		int size_kc=-1;
		int size_res=-1;
		int size_auts=-1;

		#if HAVE_CRT //Debug memory
			commandAuth = (uint8_t*)calloc((AKA_RAND_SIZE+AKA_AUTN_SIZE+2), sizeof(uint8_t));
		
		#else
			commandAuth = (uint8_t*)tsk_calloc((AKA_RAND_SIZE+AKA_AUTN_SIZE+2), sizeof(uint8_t));
		#endif //HAVE_CRT
		/*Calcule CK, IK and RES with GSM*/

		/* RFC 3310 subclause 3.2: nonce = base64(RAND || AUTN || SERV_DATA) */
		n = tsk_base64_decode((const uint8_t*)self->nonce, tsk_strlen(self->nonce), &nonce);
		if(n > TSK_MD5_STRING_SIZE){
			TSK_DEBUG_ERROR("The IMS CORE returned an invalid nonce.");
			goto bail;
		}
		if(n < AKA_RAND_SIZE + AKA_AUTN_SIZE){
			TSK_DEBUG_ERROR("The nonce returned by the IMS CORE is too short to contain both [RAND] and [AUTHN]");
			goto bail;
		}
		else{
			/* Get RAND and AUTN */
			memcpy(rand, nonce, AKA_RAND_SIZE);
			memcpy(autn, (nonce + AKA_RAND_SIZE), AKA_AUTN_SIZE);
		}

		//Byte(s)		Description				Length
		//1				Length of RAND (L1)		1
		//2 to (L1+1)	RAND					L1
		//(L1+2)		Length of AUTN (L2)		1
		//(L1+3)		(L1+L2+2)	AUTN		L2
		//Note: Parameter present if and only if in 3G security context.*/
		//Copy parameters
		commandAuth[0]=AKA_RAND_SIZE;
		memcpy(&commandAuth[1],rand,AKA_RAND_SIZE*sizeof(uint8_t));
		commandAuth[AKA_RAND_SIZE+1]=AKA_AUTN_SIZE;
		memcpy(&commandAuth[AKA_RAND_SIZE+1+1],autn,AKA_RAND_SIZE*sizeof(uint8_t));
		TSK_DEBUG_INFO("Data authentication command Auth: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
			 commandAuth[0] & 0xff, commandAuth[1] & 0xff, commandAuth[2] & 0xff,
			 commandAuth[3] & 0xff, commandAuth[4] & 0xff, commandAuth[5] & 0xff,
			 commandAuth[6] & 0xff, commandAuth[7] & 0xff, commandAuth[8] & 0xff,
			 commandAuth[9] & 0xff, commandAuth[10] & 0xff, commandAuth[11] & 0xff,
			 commandAuth[12] & 0xff, commandAuth[13] & 0xff, commandAuth[14] & 0xff,
			 commandAuth[15] & 0xff, commandAuth[16] & 0xff, commandAuth[17] & 0xff,
			 commandAuth[18] & 0xff, commandAuth[19] & 0xff, commandAuth[20] & 0xff,
			 commandAuth[21] & 0xff, commandAuth[22] & 0xff, commandAuth[23] & 0xff,
			 commandAuth[24] & 0xff, commandAuth[25] & 0xff, commandAuth[26] & 0xff,
			 commandAuth[27] & 0xff, commandAuth[28] & 0xff, commandAuth[29] & 0xff,
			 commandAuth[30] & 0xff, commandAuth[31] & 0xff, commandAuth[32] & 0xff,
			 commandAuth[33] & 0xff);
		if(!tsk_base64_encode(commandAuth, AKA_RAND_SIZE+AKA_AUTN_SIZE+2, &commandAuthBase64)){
			TSK_DEBUG_ERROR("tsk_base64_encode() failed. AKAv1 response will be invalid.");
			ret = -4;
			goto bail;
		}
		resultAka_base64=tsk_register_get_cb()(tsk_register_get_arg_data(), "" "%s" "\n", commandAuthBase64);
		//Byte(s)						Description									Length
		//1								"Successful 3G authentication" tag = 'DB'	1
		//2								Length of RES (L3)							1
		//3 to (L3+2)					RES											L3
		//(L3+3)						Length of CK (L4)							1
		//(L3+4) to (L3+L4+3)			CK											L4
		//(L3+L4+4) 					Length of IK (L5)							1
		//(L3+L4+5) to (L3+L4+L5+4)		IK											L5
		//(L3+L4+L5+5)					Length of KC (= 8)		(see note)			1
		//(L3+L4+L5+6to(L3+L4+L5+13)	KC						(see note)			8

		//Byte(s)			Description								Length
		//1					"Synchronisation failure" tag = 'DC'	1
		//2					Length of AUTS (L1)						1
		//3 to (L1+2)		AUTS									L1

		if(resultAka_base64 && resultAka_base64!=tsk_null && resultAka_base64->data && resultAka_base64->data!=tsk_null){

			AKA_RES_T akares;
			AKA_XXX_DECLARE(AK);
			AKA_XXX_DECLARE(CK);
			AKA_XXX_DECLARE(IK);
			AKA_XXX_DECLARE(K);

			AKA_XXX_BZERO(AK);
			AKA_XXX_BZERO(CK);
			AKA_XXX_BZERO(IK);
			AKA_XXX_BZERO(K);



			TSK_DEBUG_INFO("Receive response for authentication SIM");
			
			//Processing response message
			n=-1;
			n = tsk_base64_decode((const uint8_t*)resultAka_base64->data,tsk_strlen(resultAka_base64->data), &resultAka);
			if(n <= 0){
				TSK_DEBUG_ERROR("Failed to receive response by AKA");
			}else
			if(resultAka && resultAka!=tsk_null)
				switch (resultAka[0]) {
					case 0xdb:
						//"Successful 3G authentication"
						TSK_DEBUG_INFO("Successful 3G authentication");
						size_res=resultAka[1];
				
						

						//RES
						#if HAVE_CRT //Debug memory
							res = (uint8_t*)calloc((size_res), sizeof(uint8_t));
						#else
							res = (uint8_t*)tsk_calloc((size_res), sizeof(uint8_t));
						#endif //HAVE_CRT
							memcpy(res,&resultAka[2],size_res*sizeof(uint8_t));
				
						//check authentication SIM with Secret key
						
						/* Secret key */
                        if(tsk_strlen(password)==(AKA_K_SIZE*2)){
                            //It´s hex
                            #if HAVE_CRT //Debug memory
                            hex_key = malloc(tsk_strlen(password)*sizeof(uint8_t));
                            #else
                            hex_key = tsk_malloc(tsk_strlen(password)*sizeof(uint8_t));
                            #endif //HAVE_CRT
                            tsk_str_to_hex(password,tsk_strlen(password),hex_key);
                            memcpy(K, hex_key,AKA_K_SIZE);
                        }else{
                            //It´s string
                            memcpy(K, password, (tsk_strlen(password) > AKA_K_SIZE ? AKA_K_SIZE : tsk_strlen(password)));
                        }

						/* Calculate CK, IK and AK */
						f2345(K,rand, akares, CK, IK, AK);
						TSK_DEBUG_INFO("Data authentication result: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
					 akares[0] & 0xff, akares[1] & 0xff, akares[2] & 0xff,
					 akares[3] & 0xff, akares[4] & 0xff, akares[5] & 0xff, akares[6] & 0xff, akares[7] & 0xff);
					 TSK_DEBUG_INFO("Data authentication result from sim: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
					 res[0] & 0xff, res[1] & 0xff, res[2] & 0xff,
					 res[3] & 0xff, res[4] & 0xff, res[5] & 0xff, res[6] & 0xff, res[7] & 0xff);
						//CK
						size_ck=resultAka[2+size_res];
						#if HAVE_CRT //Debug memory
							ck = (uint8_t*)calloc((size_ck), sizeof(uint8_t));
						#else
							ck = (uint8_t*)tsk_calloc((size_ck), sizeof(uint8_t));
						#endif //HAVE_CRT
							memcpy(ck,&resultAka[size_res+3],size_ck*sizeof(uint8_t));
				
						//IK
						size_ik=resultAka[3+size_res+size_ck];
						#if HAVE_CRT //Debug memory
							ik = (uint8_t*)calloc((size_ik), sizeof(uint8_t));
						#else
							ik = (uint8_t*)tsk_calloc((size_ik), sizeof(uint8_t));
						#endif //HAVE_CRT
							memcpy(ik,&resultAka[size_res+size_ck+4],size_ik*sizeof(uint8_t));
						
						//KC
						size_kc=resultAka[4+size_res+size_ck+size_ik];
						#if HAVE_CRT //Debug memory
							kc = (uint8_t*)calloc((size_kc), sizeof(uint8_t));
						#else
							kc = (uint8_t*)tsk_calloc((size_kc), sizeof(uint8_t));
						#endif //HAVE_CRT
							memcpy(kc,&resultAka[size_res+size_ck+size_ik+5],size_kc*sizeof(uint8_t));
				
					


						/* RFC 4169 subclause 3
							The HTTP Digest password is derived from base64 encoded PRF(RES || IK||CK, "http-digest-akav2-password") 
							or 
							PRF(XRES||IK||CK, "http-digest-akav2-password") instead of (RES) or (XRES) respectively.
							Where PRF ==> HMAC_MD5 function.
						*/
						if(TSIP_CHALLENGE_IS_AKAv2(self)){
							uint8_t res_ik_ck[AKA_RES_SIZE + AKA_IK_SIZE + AKA_CK_SIZE];
							tsk_md5digest_t md5_digest;

							memcpy(res_ik_ck, res, AKA_RES_SIZE);
							memcpy((res_ik_ck + AKA_RES_SIZE), ik, AKA_IK_SIZE);
							memcpy((res_ik_ck + AKA_RES_SIZE + AKA_IK_SIZE), ck, AKA_CK_SIZE);

							if((ret = hmac_md5digest_compute((const uint8_t*)"http-digest-akav2-password", 26, (const char*)res_ik_ck, sizeof(res_ik_ck), md5_digest))){/* PRF(RES||IK||CK, ...) */
								TSK_DEBUG_ERROR("hmac_md5digest_compute() failed. AKAv2 response will be invalid.");

								ret = -3;
								goto bail;
							}
							else{/* b64(PRF(...)) */
								if(!tsk_base64_encode(md5_digest, sizeof(md5_digest), result)){
									TSK_DEBUG_ERROR("tsk_base64_encode() failed. AKAv2 response will be invalid.");

									ret = -4;
									goto bail;
								}
							}
						}
						else{
							#if HAVE_CRT //Debug memory
							*result = calloc(1, AKA_RES_SIZE + 1);
		
						#else
							*result = tsk_calloc(1, AKA_RES_SIZE + 1);
		
						#endif //HAVE_CRT
							memcpy(*result, res, AKA_RES_SIZE);
							TSK_DEBUG_INFO("Data authentication result: %s\n",*result);
							ret = 0;
						}
				
						/* Copy CK and IK */
						if(ck && ck!=tsk_null)
							memcpy(self->ck, ck,  ( size_ck>= AKA_CK_SIZE ? AKA_CK_SIZE : size_ck));
						if(ik && ik!=tsk_null)
							memcpy(self->ik, ik, ( size_ik>= AKA_IK_SIZE ? AKA_IK_SIZE : size_ik));
						/* Secret key */
						if(kc && kc!=tsk_null)
						memcpy(self->k, kc, (size_kc > AKA_K_SIZE ? AKA_K_SIZE : size_kc));

						TSK_FREE(ck);
						TSK_FREE(ik);

						break;

					case 0xdc:
						
						//"Synchronisation failure"
						TSK_DEBUG_WARN("Synchronisation failure");
						size_auts=resultAka[1];
						//AUTS
						#if HAVE_CRT //Debug memory
							*auts_bin = (uint8_t*)calloc((size_auts), sizeof(uint8_t));
						#else
							*auts_bin = (uint8_t*)tsk_calloc((size_auts), sizeof(uint8_t));
						#endif //HAVE_CRT
						memcpy(*auts_bin,&resultAka[2],(size_auts > AKA_AUTS_SIZE ? AKA_AUTS_SIZE : size_auts)*sizeof(uint8_t));
				
						
						break;
					default:
						TSK_DEBUG_ERROR("Response isn´t logic in authentication: %02x",resultAka[0] & 0xff);

						ret=-4;
						goto bail;
						break;
				}
			}else{
				TSK_DEBUG_ERROR("it�s no ok the data");
			}
	}else{
		TSK_DEBUG_INFO("Use authentication NO SIM");
		#define SQN_XOR_AK() (AUTN + 0)
		#define SERVER_DATA() (nonce + AKA_RAND_SIZE + AKA_AUTN_SIZE)
		
			// � ==> XOR
			// || ==> append
		
			AKA_RES_T akares;
		
			
			int i=0;
			tsk_size_t n;
			
			//uint8_t sqn_ms[AKA_SQN_SIZE];
			uint8_t sqn_he[AKA_SQN_SIZE]= {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
			
			
		
		
			AKA_XXX_DECLARE(RAND);
			AKA_XXX_DECLARE(AK);
			AKA_XXX_DECLARE(AMF);
			AKA_XXX_DECLARE(CK);
			AKA_XXX_DECLARE(IK);
			AKA_XXX_DECLARE(K);
			AKA_XXX_DECLARE(SQN);
			AKA_XXX_DECLARE(MAC_A);
			AKA_XXX_DECLARE(AUTN);
		
			AKA_XXX_BZERO(RAND);
			AKA_XXX_BZERO(AK);
			AKA_XXX_BZERO(AMF);
			AKA_XXX_BZERO(CK);
			AKA_XXX_BZERO(IK);
			AKA_XXX_BZERO(K);
			AKA_XXX_BZERO(SQN);
			AKA_XXX_BZERO(MAC_A);
			AKA_XXX_BZERO(AUTN);
		
			#if HAVE_CRT //Debug memory
				*auts_bin = (uint8_t*)calloc((AKA_AUTS_SIZE), sizeof(uint8_t));
				
			#else
				*auts_bin = (uint8_t*)tsk_calloc((AKA_AUTS_SIZE), sizeof(uint8_t));
				
			#endif //HAVE_CRT
		
			/* RFC 3310 subclause 3.2: nonce = base64(RAND || AUTN || SERV_DATA) */
			n = tsk_base64_decode((const uint8_t*)self->nonce, tsk_strlen(self->nonce), &nonce);
			if(n > TSK_MD5_STRING_SIZE){
				TSK_DEBUG_ERROR("The IMS CORE returned an invalid nonce.");
				goto bail;
			}
			if(n < AKA_RAND_SIZE + AKA_AUTN_SIZE){
				TSK_DEBUG_ERROR("The nonce returned by the IMS CORE is too short to contain both [RAND] and [AUTHN]");
				goto bail;
			}
			else{
				/* Get RAND and AUTN */
				memcpy(RAND, nonce, AKA_RAND_SIZE);
				memcpy(AUTN, (nonce + AKA_RAND_SIZE), AKA_AUTN_SIZE);
			}
		
			/* Secret key */
        /* Secret key */
        if(tsk_strlen(password)==(AKA_K_SIZE*2)){
            //It´s hex
        #if HAVE_CRT //Debug memory
                    hex_key = malloc(tsk_strlen(password)*sizeof(uint8_t));
        #else
                    hex_key = tsk_malloc(tsk_strlen(password)*sizeof(uint8_t));
        #endif //HAVE_CRT
                    tsk_str_to_hex(password,tsk_strlen(password),hex_key);
                    memcpy(K, hex_key,AKA_K_SIZE);
                }else{
            //It´s string
            memcpy(K, password, (tsk_strlen(password) > AKA_K_SIZE ? AKA_K_SIZE : tsk_strlen(password)));
        }
		
			/* 3GPP TS 35.205: AUTN = SQN[�AK] || AMF || MAC-A */
			memcpy(AMF, (AUTN + AKA_SQN_SIZE), AKA_AMF_SIZE);
			memcpy(MAC_A, (AUTN + AKA_SQN_SIZE + AKA_AMF_SIZE), AKA_MAC_A_SIZE);
		
			/* compute OP */
			ComputeOP(TSIP_CHALLENGE_STACK(self)->security.operator_id);
		
			/* Checks that we hold the same AMF */
			for(n=0; n<AKA_AMF_SIZE; n++){
				if(AMF[n] != TSIP_CHALLENGE_STACK(self)->security.amf[n]){
					TSK_DEBUG_ERROR("IMS-AKA error: AMF <> XAMF");
					goto bail;
				}
			}
		
			/* Calculate CK, IK and AK */
			f2345(K, RAND, akares, CK, IK, AK);
			
		
			/* Calculate SQN from SQN_XOR_AK */
			/*
			SQN is optenido of the nonce and must be checked to be equal to the stored for the client. 
			Where it is not what is should be general AUTS parameter to resynchronize the SQN between
			the client and the HSS
			*/
			for(n=0; n<AKA_SQN_SIZE; n++){
				SQN[n] = (uint8_t) (SQN_XOR_AK()[n] ^ AK[n]);
			}
			TSK_DEBUG_INFO("Data authentication SQN: %02x:%02x:%02x:%02x:%02x:%02x\n",
		         SQN[0] & 0xff, SQN[1] & 0xff, SQN[2] & 0xff,
		         SQN[3] & 0xff, SQN[4] & 0xff, SQN[5] & 0xff);
			TSK_DEBUG_INFO("Data authentication SQN XOR AK: %02x:%02x:%02x:%02x:%02x:%02x\n",
		         SQN_XOR_AK()[0] & 0xff, SQN_XOR_AK()[1] & 0xff, SQN_XOR_AK()[2] & 0xff,
		         SQN_XOR_AK()[3] & 0xff, SQN_XOR_AK()[4] & 0xff, SQN_XOR_AK()[5] & 0xff);
			TSK_DEBUG_INFO("Data authentication AK: %02x:%02x:%02x:%02x:%02x:%02x\n",
		         AK[0] & 0xff, AK[1] & 0xff, AK[2] & 0xff,
		         AK[3] & 0xff, AK[4] & 0xff, AK[5] & 0xff);
		
		
			/* Calculate XMAC_A */
			{
				AKA_MAC_A_T XMAC_A;
				memset(XMAC_A, '\0', sizeof(XMAC_A));
				
				f1(K, RAND, SQN, AMF, XMAC_A);
				if(!tsk_strnequals(MAC_A, XMAC_A, AKA_MAC_A_SIZE)){
					TSK_DEBUG_ERROR("IMS-AKA error: XMAC_A [%s] <> MAC_A[%s]", XMAC_A, MAC_A);
					goto bail;
				}else{
					TSK_DEBUG_INFO("Data:\n xmac=[%s]\nmax=[%s]", XMAC_A, MAC_A);
					TSK_DEBUG_INFO("Data authentication XMAC_A: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
					 XMAC_A[0] & 0xff, XMAC_A[1] & 0xff, XMAC_A[2] & 0xff,
					 XMAC_A[3] & 0xff, XMAC_A[4] & 0xff, XMAC_A[5] & 0xff, XMAC_A[6] & 0xff, XMAC_A[7] & 0xff)
					TSK_DEBUG_INFO("Data authentication MAC_A: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
					 MAC_A[0] & 0xff, MAC_A[1] & 0xff, MAC_A[2] & 0xff,
					 MAC_A[3] & 0xff, MAC_A[4] & 0xff, MAC_A[5] & 0xff, MAC_A[6] & 0xff, XMAC_A[7] & 0xff)

				}
			}
			
			/*
			//Test
			//Check SQN han generate AUTS
			sqn_ms[5] = sqn_he[5] + 1;
		    f5star(K, RAND, AK);
		    for(i=0; i<AKA_SQN_SIZE; i++)
		        (*auts_bin)[i]=sqn_ms[i]^AK[i];
		    f1star(K, RAND, sqn_ms, AMF, (uint8_t * ) ((*auts_bin)+AKA_SQN_SIZE));
			TSK_DEBUG_INFO("Data authentication AUTS BIN: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
		         (*auts_bin)[0] & 0xff, (*auts_bin)[1] & 0xff, (*auts_bin)[2] & 0xff,
		         (*auts_bin)[3] & 0xff, (*auts_bin)[4] & 0xff, (*auts_bin)[5] & 0xff,
				 (*auts_bin)[6] & 0xff, (*auts_bin)[7] & 0xff, (*auts_bin)[8] & 0xff,
		         (*auts_bin)[9] & 0xff, (*auts_bin)[10] & 0xff, (*auts_bin)[11] & 0xff,
				 (*auts_bin)[12] & 0xff, (*auts_bin)[13] & 0xff);
			*/
			/* RFC 4169 subclause 3
				The HTTP Digest password is derived from base64 encoded PRF(RES || IK||CK, "http-digest-akav2-password") 
				or 
				PRF(XRES||IK||CK, "http-digest-akav2-password") instead of (RES) or (XRES) respectively.
				Where PRF ==> HMAC_MD5 function.
			*/
			if(TSIP_CHALLENGE_IS_AKAv2(self)){
				uint8_t res_ik_ck[AKA_RES_SIZE + AKA_IK_SIZE + AKA_CK_SIZE];
				tsk_md5digest_t md5_digest;
		
				memcpy(res_ik_ck, akares, AKA_RES_SIZE);
				memcpy((res_ik_ck + AKA_RES_SIZE), IK, AKA_IK_SIZE);
				memcpy((res_ik_ck + AKA_RES_SIZE + AKA_IK_SIZE), CK, AKA_CK_SIZE);
		
				if((ret = hmac_md5digest_compute((const uint8_t*)"http-digest-akav2-password", 26, (const char*)res_ik_ck, sizeof(res_ik_ck), md5_digest))){/* PRF(RES||IK||CK, ...) */
					TSK_DEBUG_ERROR("hmac_md5digest_compute() failed. AKAv2 response will be invalid.");
		
					ret = -3;
					goto bail;
				}
				else{/* b64(PRF(...)) */
					if(!tsk_base64_encode(md5_digest, sizeof(md5_digest), result)){
						TSK_DEBUG_ERROR("tsk_base64_encode() failed. AKAv2 response will be invalid.");
		
						ret = -4;
						goto bail;
					}
				}
			}
			else{
				#if HAVE_CRT //Debug memory
				*result = calloc(1, AKA_RES_SIZE + 1);
				
			#else
				*result = tsk_calloc(1, AKA_RES_SIZE + 1);
				
			#endif //HAVE_CRT
				memcpy(*result, akares, AKA_RES_SIZE);
				TSK_DEBUG_INFO("Data authentication result: %s\n",*result);
				TSK_DEBUG_INFO("Data authentication result: %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n",
					 akares[0] & 0xff, akares[1] & 0xff, akares[2] & 0xff,
					 akares[3] & 0xff, akares[4] & 0xff, akares[5] & 0xff, akares[6] & 0xff, akares[7] & 0xff)
				ret = 0;
			}
		
			/* Copy CK and IK */
			memcpy(self->ck, CK, AKA_CK_SIZE);
			memcpy(self->ik, IK, AKA_IK_SIZE);
	}
	


bail:
	TSK_FREE(nonce);
	return ret;

//#undef SQN_XOR_AK
//#undef SERVER_DATA
}

int tsip_challenge_get_response(tsip_challenge_t *self, const char* method, const char* uristring, const tsk_buffer_t* entity_body,char** auts_base64, tsk_md5string_t* response)
{

	
	if(TSIP_CHALLENGE_IS_DIGEST(self) && self->stack){
		tsk_md5string_t ha1, ha2;
		nonce_count_t nc;

		/* ===
			Calculate HA1 = MD5(A1) = M5(username:realm:secret)
			In case of AKAv1-MD5 and AKAv2-MD5 the secret must be computed as per RFC 3310 + 3GPP TS 206/7/8/9.
			The resulting AKA RES parameter is treated as a "password"/"secret" when calculating the response directive of RFC 2617.
		*/
		if(TSIP_CHALLENGE_IS_AKAv1(self) || TSIP_CHALLENGE_IS_AKAv2(self)){
			char* akaresult = tsk_null;
			uint8_t* auts_bin=tsk_null;
			tsip_challenge_get_akares(self, TSIP_CHALLENGE_STACK(self)->identity.password,&auts_bin, &akaresult);
			
			if(akaresult && akaresult!=tsk_null && thttp_auth_digest_HA1(TSIP_CHALLENGE_USERNAME(self), self->realm, akaresult, &ha1)){
				// return -1;
			}
			
			if(auts_bin && auts_bin!=tsk_null){
				//Encode auts base64
				char* autsBase64_temp=tsk_null;
				tsk_size_t size=0;
				size=tsk_base64_encode((auts_bin),AKA_AUTS_SIZE*sizeof(uint8_t), &autsBase64_temp);
				
				if(((size>0 && autsBase64_temp) && autsBase64_temp!=tsk_null)){
					TSK_DEBUG_INFO("Generate AUTS: %s",autsBase64_temp);
					*auts_base64=strdup (autsBase64_temp);
				}
				
				//if(auts_base64->data)
				//TSK_DEBUG_INFO("Data authentication AUTS Base64: %s",(char*)auts_base64->data);
				
			}
			
			TSK_DEBUG_INFO("Response in authentication HA1: %s",ha1);
			TSK_FREE(akaresult);
			TSK_FREE(auts_bin);
		}
		else{
			if(!tsk_strnullORempty(self->ha1_hexstr)){
				// use HA1 provide be the user (e.g. webrtc2sip server will need this to authenticate INVITEs when acting as b2bua)
				memset(ha1, 0, sizeof(tsk_md5string_t));
				memcpy(ha1, self->ha1_hexstr, (TSK_MD5_DIGEST_SIZE << 1));
			}
			else{
				thttp_auth_digest_HA1(TSIP_CHALLENGE_USERNAME(self), self->realm, TSIP_CHALLENGE_STACK(self)->identity.password, &ha1);
			}
		}

		/* ===
			HA2 
		*/
		thttp_auth_digest_HA2(method,
			uristring,
			entity_body,
			self->qop,
			&ha2);
		TSK_DEBUG_INFO("Response in authentication HA2: %s",ha2);
		/* RESPONSE */
		if(self->nc){
			THTTP_NCOUNT_2_STRING(self->nc, nc);
		}
		thttp_auth_digest_response((const tsk_md5string_t *)&ha1, 
			self->nonce,
			nc,
			self->cnonce,
			self->qop,
			(const tsk_md5string_t *)&ha2,
			response);
		if(self->qop){
			self->nc++;
		}

		return 0;
	}
	return -1;
}

int tsip_challenge_update(tsip_challenge_t *self, const char* scheme, const char* realm, const char* nonce, const char* opaque, const char* algorithm,const char* auts,const char* auts_param,const char* auts_value, const char* qop)
{
	if(self){
		int noncechanged = !tsk_striequals(self->nonce, nonce);

		tsk_strupdate(&self->scheme, scheme);
		tsk_strupdate(&self->realm, realm);
		tsk_strupdate(&self->nonce, nonce);
		tsk_strupdate(&self->opaque, opaque);
		tsk_strupdate(&self->algorithm, algorithm);
		tsk_strupdate(&self->auts, auts);
		tsk_strupdate(&self->auts_param, auts_param);
		tsk_strupdate(&self->auts_value, auts_value);
		if(qop){
			self->qop = tsk_strcontains(qop, tsk_strlen(qop), "auth-int") ? "auth-int" : 
					(tsk_strcontains(qop, tsk_strlen(qop), "auth") ? "auth" : tsk_null);
		}

		if(noncechanged && self->qop){
			tsip_challenge_reset_cnonce(self);
		}
		return 0;
	}
	return -1;
}

int tsip_challenge_set_cred(tsip_challenge_t *self, const char* username, const char* ha1_hexstr)
{
	if(!self || tsk_strlen(ha1_hexstr) != (TSK_MD5_DIGEST_SIZE << 1)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	tsk_strupdate(&self->username, username);
	tsk_strupdate(&self->ha1_hexstr, ha1_hexstr);
	return 0;
}

tsip_header_t *tsip_challenge_create_header_authorization(tsip_challenge_t *self, const tsip_request_t *request)
{
	tsk_md5string_t response;
	char* auts=tsk_null;
	nonce_count_t nc;
	char *uristring = tsk_null;
	tsip_header_t *header = tsk_null;
	
	if(!self || !self->stack || !request){
		goto bail;
	}

	if(!(uristring = tsip_uri_tostring(request->line.request.uri, tsk_true, tsk_false))){
		TSK_DEBUG_ERROR("Failed to parse URI: %s", uristring);
		goto bail;
	}

	/* We compute the nc here because @ref tsip_challenge_get_response function will increment it's value. */
	if(self->nc){
		THTTP_NCOUNT_2_STRING(self->nc, nc);
	}


	/* entity_body ==> request-content */
	if(tsip_challenge_get_response(self, request->line.request.method, uristring, request->Content,&auts, &response)){
		goto bail;
	}else if(auts && auts!=tsk_null){
		TSK_DEBUG_INFO("Nonce in authentication auts base64: %s",auts);
	}
	
	TSK_DEBUG_INFO("Nonce in authentication: %s",self->nonce);
	TSK_DEBUG_INFO("Response in authentication: %s",response);

#define TSIP_AUTH_COPY_VALUES(hdr)															\
		hdr->username = tsk_strdup(TSIP_CHALLENGE_USERNAME(self));							\
		hdr->scheme = tsk_strdup(self->scheme);												\
		hdr->realm = tsk_strdup(self->realm);												\
		hdr->nonce = tsk_strdup(self->nonce);												\
		hdr->qop = tsk_strdup(self->qop);													\
		hdr->opaque = tsk_strdup(self->opaque);												\
		hdr->algorithm = self->algorithm ? tsk_strdup(self->algorithm) : tsk_strdup("AKAv1-MD5");	\
		hdr->cnonce = self->nc? tsk_strdup(self->cnonce) : tsk_null;						\
		hdr->uri = tsk_strdup(uristring);													\
		hdr->nc = self->nc? tsk_strdup(nc) : 0;												\
		hdr->response = tsk_strdup(response);												\
		hdr->auts = tsk_strdup(auts);													\
		hdr->auts_param = tsk_strdup(self->auts_param);													\
		hdr->auts_value = tsk_strdup(self->auts_value);													\

		

	if(self->isproxy){
		tsip_header_Proxy_Authorization_t *proxy_auth = tsip_header_Proxy_Authorization_create();
		TSIP_AUTH_COPY_VALUES(proxy_auth);
		header = TSIP_HEADER(proxy_auth);
	}
	else{
		tsip_header_Authorization_t *auth = tsip_header_Authorization_create();
		TSIP_AUTH_COPY_VALUES(auth);
		header = TSIP_HEADER(auth);
	}

bail:
	TSK_FREE(uristring);

	return header;

#undef TSIP_AUTH_COPY_VALUES
}

tsip_header_t *tsip_challenge_create_empty_header_authorization(const char* username, const char* realm, const char* uristring)
{
	tsip_header_Authorization_t *header = tsip_header_Authorization_create();

	if(header){
		header->scheme = tsk_strdup("Digest");
		header->username = tsk_strdup(username);
		header->realm = tsk_strdup(realm);
		header->nonce = tsk_strdup("");
		header->response = tsk_strdup("");
		header->uri = tsk_strdup(uristring);
		//header->algorithm = tsk_strdup("");
		//header->algorithm = tsk_strdup("AKAv1-MD5");
		
	}

	return TSIP_HEADER(header);
}

























//========================================================
//	SIP challenge object definition
//

/**@ingroup tsip_challenge_group
*/
static tsk_object_t* tsip_challenge_ctor(tsk_object_t *self, va_list * app)
{
	tsip_challenge_t *challenge = self;
	if(challenge){
		const char* qop;

		challenge->stack = va_arg(*app, const tsip_stack_handle_t *);
		challenge->isproxy = va_arg(*app, tsk_bool_t);
		challenge->username = tsk_strdup(((const struct tsip_stack_s*)challenge->stack)->identity.impi);
		challenge->scheme = tsk_strdup(va_arg(*app, const char*));
		challenge->realm = tsk_strdup(va_arg(*app, const char*));
		challenge->nonce = tsk_strdup(va_arg(*app, const char*));
		challenge->opaque = tsk_strdup(va_arg(*app, const char*));
		challenge->algorithm = tsk_strdup(va_arg(*app, const char*));
		//
		//This is for SQN with SIM
		challenge->auts = tsk_strdup(va_arg(*app, const char*));
		challenge->auts_param = tsk_strdup(va_arg(*app, const char*));
		challenge->auts_value = tsk_strdup(va_arg(*app, const char*));
		qop = va_arg(*app, const char*);
		if(qop){
			challenge->qop = tsk_strcontains(qop, tsk_strlen(qop), "auth-int") ? "auth-int" : 
					(tsk_strcontains(qop, tsk_strlen(qop), "auth") ? "auth" : tsk_null);
		}
		
		if(challenge->qop){
			tsip_challenge_reset_cnonce(challenge);
		}
	}
	else TSK_DEBUG_ERROR("Failed to create new sip challenge object.");
	
	return self;
}

/**@ingroup tsip_challenge_group
*/
static tsk_object_t* tsip_challenge_dtor(tsk_object_t *self)
{
	tsip_challenge_t *challenge = self;
	if(challenge){
		TSK_FREE(challenge->username);
		TSK_FREE(challenge->scheme);
		TSK_FREE(challenge->realm);
		TSK_FREE(challenge->nonce);
		TSK_FREE(challenge->opaque);
		TSK_FREE(challenge->algorithm);
		//
		//This is for SQN with SIM
		TSK_FREE(challenge->auts);
		TSK_FREE(challenge->auts_param);
		TSK_FREE(challenge->auts_value);
		TSK_FREE(challenge->ha1_hexstr);
	}
	else{
		TSK_DEBUG_ERROR("Null SIP challenge object.");
	}

	return self;
}

static const tsk_object_def_t tsip_challenge_def_s = 
{
	sizeof(tsip_challenge_t),
	tsip_challenge_ctor,
	tsip_challenge_dtor,
	tsk_null
};
const tsk_object_def_t *tsip_challenge_def_t = &tsip_challenge_def_s;
