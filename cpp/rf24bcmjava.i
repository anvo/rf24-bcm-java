%module rf24bcmjava
%include "stdint.i"
%include "typemaps.i"

/* Map void* buff to byte[] in read and write methods */
%typemap(jni) const void* buf, void *buf, const uint8_t *address	"jbyteArray"
%typemap(jstype) const void* buf, void *buf, const uint8_t *address	"byte[]"
%typemap(jtype) const void* buf, void *buf, const uint8_t *address	"byte[]"

%typemap(javain) const void* buf, void *buf, const uint8_t *address	"$1_name"

%typemap(in)	const void* buf, void *buf, const uint8_t *address	{
$1 = ($1_ltype) JCALL2(GetPrimitiveArrayCritical, jenv, $input, 0);
}

%typemap(freearg)	const void* buf, void *buf, const uint8_t *address	{
JCALL3(ReleasePrimitiveArrayCritical, jenv, $input, $1, 0);
}

/* Map const uint8_t *address to byte[] in openReadingPipe and openWritingPipe methods  */

/* Output parameters for RF24::whatHappened*/
%apply bool &OUTPUT { bool &tx_ok, bool &tx_fail,bool &rx_ready};

/* Output parameter for RF24::available(uint8_t* pipe_num) */
%apply uint8_t *OUTPUT {uint8_t* pipe_num};


%{
#include "RF24.h"
%}
%include "RF24/RPi/RF24/RF24.h"