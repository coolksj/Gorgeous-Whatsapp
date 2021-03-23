# Gorgeous

<a href="https://paypal.me/gorgeous007" target="_blank"><img src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif" /></a>

The WhatsApp lib

    Yowsup is no longer updated. There is no WhatsApp library available, but many people need it. I will gradually open source the library I have used for a long time. This library is written in Java. Will include all features except registration. Because the registration part is not in the scope of this open source.
# If this code is useful for you, please give me a STAR

# login
![main](https://raw.githubusercontent.com/lovethiscode/Gorgeous-Whatsapp/main/images/main.png)

# addcontact
![addcontact](https://raw.githubusercontent.com/lovethiscode/Gorgeous-Whatsapp/main/images/addcontact.png)

# updatehead
![updatehead](https://raw.githubusercontent.com/lovethiscode/Gorgeous-Whatsapp/main/images/updatehead.png)

# sendmessage
![sendmessage](https://raw.githubusercontent.com/lovethiscode/Gorgeous-Whatsapp/main/images/sendmessage.png)

# How to log in

 First of all, you need to have an account. This account can be exported from the mobile phone or simulator by using tools. What you export is a database file, which contains all the necessary information of the account. The phone and emulator must be root, otherwise they cannot be exported. The tools will be open source

 1) Use the emulator or mobile phone to register a WhatsApp account. The emulator and mobile phone need to have root permission.

 2) After successful registration, install the extraction tool (APK) to extract account information. The extraction tool is open source and can be modified by yourself.

 3) Log in with the extracted account information.


# warning
    Application will visit https://www.whatsapp.com/android/ to check the version of whatsapp, please confirm whether your network can normal connection, try again or set agent.
    To avoid version discrepancies, call the NoiseJni.CheckWhatsappVersion function once to check the version. 

#Linux
	sudo apt install libssl-dev
	sudo apt-get install curl libcurl4-openssl-dev

# Step (All is complete)
  1) login
  2) Send text message 
  3) Send media message(video, audio,image, document ...)
  4) Receive message(text, media ...)
  5) group (create, join, leave ...)
  6) presence

## How to use
Tool
1) Compiling APK with Android studio
2) Install APK to your emulator or mobile phone (need root)
3) Export database
4) Copy the database to the out directory, and log in with Gorgeous.



Gorgeous
1) Open the Gorgeous project with IntelliJ idea and you can compile it directly.

2) Copy axolotl.db  to the out directory.

3) click login button, then you can login with the default account, receive server response.


## 如何使用
Tool
 这是一个安卓工程，用来从手机或者模拟器导出 账号数据，导出的账号数据可以使用 Gorgeous 登录。
 1) 使用android studio 编译tool 工程
 2) 将编译出的 apk 安装到手机或者模拟器上(需要有root 权限)
 3) 打开apk， 直接点击导出就可以生成数据库文件。

 Gorgeous 
 1) 使用 IntelliJ idea 打开 Gorgeous 工程，可以直接启动编译
 2) 将测试数据库 axolotl.db 拷贝到out 目录下。或者也可以修改demo代码 加载指定位置的数据库。

 3) 可能需要修改测试代码，指定socket 连接的代理，否则某些地方会连接不上服务器， 编译成功之后点击 界面上的登录即可。


# License:

Gorgeous is licensed under the GPLv3+: http://www.gnu.org/licenses/gpl-3.0.html.


