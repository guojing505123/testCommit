Action()
{

	web_url("dimana.gif", 
		"URL=http://pvstat.qihoo.com/dimana.gif?_pdt=360se6bq&_dim_k=pid__guid__ver&_dim_v=360se6__{c6f43ec9ee117658a5f5ec23391a27f7}__8.1.1.230&m=0&mts=0&iname=360se6&ifver=0.0.0.0&imod=bbhook.dll&imodver=0.0.0.0&icode=0xc0000005&ioff=0x000026ec&ios=6.2.9200&iie=10.0.9200.17267&mid=c6f43ec9ee117658a5f5ec23391a27f7&pt=main_process&pid=360se", 
		"Resource=1", 
		"Referer=", 
		LAST);

	web_submit_data("crashinfo", 
		"Action=http://crash.browser.360.cn/interface/crashinfo/", 
		"Method=POST", 
		"EncType=multipart/form-data", 
		"RecContentType=text/html", 
		"Referer=", 
		"Snapshot=t1.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=src", "Value=8", ENDITEM, 
		"Name=val", "Value=F0Oz3OkPlBdJ/eqCD1FfUDzyuowSxE9jAL5KR8fcZIHnuchJx63+rHMe7rI3N3UbHMIWPcFD+uv1Sz/vm7tpy95j6A82uj9V61oJ137noB3t7kr9oXojsN4GTcLLeIWpGHSqhoZ+xrw2BI5S2EpmFrXqON7o4KWoad46Gx51UFUr1Jm5fM/KwoGjFbZ0sq3t8cUTFnNCsXjNS89gT8ZKC+dN1ox+nQgGCybW+YlgTcyJFGLQu/56NZNaGmstVtJGYIjUMqeO9r2pKjwAwIGy44Tvc5W9U4n3B0e6lK4iOSOTqZwCGY4A8/+PLnURXrE0CXuhJw/ogEWmb3pqHWKMHSbuXo/7woxyEF0kha075a2fuzXHn8IQEaitNbW+"
		"RLYQPAys4N4dNtgPBrquk8VpQyNmOWSVzhdw00odmFtdVGsKvH3RLWurvfC5wjFhwLF46VRGsZfF9NfsaAaHPHg2z27FxBgE9hwpzArSL2ki/4UdVCsNjNXJqVw2iQ94/ghwM0dmIIw3weGLmXpG7Qj6/t/m9BY4P9Oo83Vhp/pTNfk7OhrTczoUQCuetzr0c8iq", ENDITEM, 
		LAST);

	web_submit_data("dumpinfo", 
		"Action=http://crash.browser.360.cn/interface/dumpinfo/", 
		"Method=POST", 
		"EncType=multipart/form-data", 
		"RecContentType=text/html", 
		"Referer=", 
		"Snapshot=t2.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=src", "Value=8", ENDITEM, 
		"Name=id", "Value=2016091020945599", ENDITEM, 
		"Name=file", "Value=8887203_8537_secrash_main_process.dmp", "File=Yes", ENDITEM, 
		"Name=cverify", "Value=37a598b1cca3c49538ac73a63e97dac4", ENDITEM, 
		LAST);

	return 0;
}