SendOSC
====
SendOSC is a simple OSC tool for Android.

Google Play Store
* https://play.google.com/store/apps/details?id=net.sabamiso.android.sendosc4a

Demo movie
* http://youtu.be/yW12hzykZr0
* [![ScreenShot](http://i.gyazo.com/53cdcf821c9d25bcb458e5cab63ef156.png)](http://youtu.be/yW12hzykZr0)

Where is settings activity ?
----
You can display settings activity by long-pressing.

Message format
----

    format:
        address type0 arg0 type1 arg1 type2 arg2 ....

        address : OSC Message Address
        type    : Argument type. i:int, f:float, s:string
        args    : OSC Message arguments

    ex:
        /addr0 i 123
        /addr1 f 123.45 s texttext
        /addr2 s texttext
        /addr3 i 123 f 123.45 s texttext

see also...
* https://github.com/yoggy/sendosc

Libraries
----
SendOSC uses the following libraries.

Java OSC
* http://www.illposed.com/software/javaosc.html

JmDNS
* http://jmdns.sourceforge.net/
