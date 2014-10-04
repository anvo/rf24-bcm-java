JAVA_INCLUDE=/usr/lib/jvm/j2sdk1.7-oracle/include

clean:
	rm cpp/RF24_wrap.o
	rm cpp/librf24bcmjava.so

# Create Java JNI and C++ wrapper classes 
swig:
	swig -v -java -c++ -outdir ./java/com/github/stanleyseow/ -o ./cpp/RF24_wrap.cpp -package com.github.stanleyseow ./cpp/rf24bcmjava.i

# Create C++ wrapper library
wrapper:
	g++ -fPIC -c cpp/RF24_wrap.cpp -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE)/linux -I./cpp/RF24/RPi/RF24 -o cpp/RF24_wrap.o -include cpp/RF24/RPi/RF24/RF24.h
	gcc -shared -lstdc++  cpp/RF24_wrap.o cpp/RF24/RPi/RF24/RF24.o cpp/RF24/RPi/RF24/bcm2835.o -o cpp/librf24bcmjava.so 
	
# Start example
gettingstarted:
	java -Djava.library.path=cpp/ -Djava.class.path=bin/ GettingStarted 
	