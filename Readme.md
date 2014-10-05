# rf24-bcm-java


Java wrapper for the nRF24L01 library [RF24](https://github.com/stanleyseow/RF24) using [SWIG](http://www.swig.org/) and JNI.

# Setup

1. `git clone git@github.com:anvo/rf24-bcm-java.git`
1. `cd rf24-bcm-java/`
1. `git submodule init`
1. `git submodule update`
1. `cd cpp/RF24/RPi/RF24/`
1. `make`
1. `cd ../../../../`
1. `make wrapper`

# Examples

See [examples/](https://github.com/anvo/rf24-bcm-java/tree/master/examples) folder.

# Run
`java -Djava.library.path=cpp/ -Djava.class.path=bin/ GettingStarted`

Simply upload the default RF24 GettingStarted sketch onto your Arduino and start the connection.

# Download

For the **Raspberry Pi** you can download the following compiled binaries:

* nRF24L01 + JNI Wrapper library [librf24bcmjava.so](http://anvo.github.io/rf24-bcm-java/download/librf24bcmjava.so)
* Java wrapper jar [rf24-bcm-java.jar](http://anvo.github.io/rf24-bcm-java/download/rf24-bcm-java.jar)

#### Run
To run the included GettingStarted example:

`java -Djava.library.path=. -jar rf24-bcm-java.jar`

#### Error

You may get the following error:

    bcm2835_init: Unable to open /dev/mem: Permission denied
    #
    # A fatal error has been detected by the Java Runtime Environment: 
    #
    #  SIGSEGV (0xb) at pc=0x42d3077c, pid=27571, tid=1086088304

The nRF24L01 library requires access to `/dev/mem` which is by default only possible for root:

`sudo java -Djava.library.path=. -jar rf24-bcm-java.jar`