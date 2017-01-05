Action()
{

	int a;  
	a=rand()%(892-2655);  
	lr_save_int(a,"loginid")+2015;
	
	web_submit_data("crashinfo", 
		"Action=http://192.168.11.198:8080/enspCom/uploadUser", 
		"Method=POST", 
		"EncType=multipart/form-data", 
		"RecContentType=text/html", 
		"Referer=", 
		"Snapshot=t1.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=phone", "Value=123456", ENDITEM, 
		"Name=syscode", "Value=enspApply", ENDITEM,
					"Name=username", "Value=123456", ENDITEM, 
					"Name=loginid", "Value={loginid}", ENDITEM, 
					"Name=e2id", "Value=123456", ENDITEM, 
					"Name=idcard", "Value=123456", ENDITEM, 
					"Name=gender", "Value=0", ENDITEM, 
		LAST);

    
	return 0;
}