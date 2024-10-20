/*
 * ===================================================================
 *  TS 26.104
 *  REL-5 V5.4.0 2004-03
 *  REL-6 V6.1.0 2004-03
 *  3GPP AMR Floating-point Speech Codec
 * ===================================================================
 *
 */

/*
 * interf_dec.h
 *
 *
 * Project:
 *    AMR Floating-Point Codec
 *
 * Contains:
 *    Defines interface to AMR decoder
 *
 */

#ifndef _interf_dec_h_
#define _interf_dec_h_

#if _MSC_VER // this is defined when compiling with Visual Studio
#define EXPORT_API __declspec(dllexport) // Visual Studio needs annotating exported functions with this
#else
#define EXPORT_API // XCode does not need annotating exported functions, so define is empty
#endif

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Function prototypes
 */
/*
 * Conversion from packed bitstream to endoded parameters
 * Decoding parameters to speech
 */
	EXPORT_API void Decoder_Interface_Decode(void *st,

#ifndef ETSI
      unsigned char *bits,

#else
      short *bits,
#endif

      short *synth, int bfi );

/*
 * Reserve and init. memory
 */
	EXPORT_API void * Decoder_Interface_init();

/*
 * Exit and free memory
 */
	EXPORT_API void Decoder_Interface_exit(void *state);
#ifdef __cplusplus
}
#endif
#endif

