//引入包
var page = require("webpage").create();
var fs = require("fs");
page.open("http://seckillpjb/resources/getitem.html?id=2",function(status){
	console.log("status = " + status);
    //每隔1秒就尝试一次，防止JS没加载完
    var isInit = "0";
    setInterval(function(){
        if(isInit != "1"){
            //手动执行一次initView
            page.evaluate(function(){
                initView();
            })
            //手动设置hasInit
            isInit = page.evaluate(function(){
                return hasInit();
            })
        } else {
            fs.write("getitem.html",page.content,"w");
            phantom.exit();
        }
    },1000);
})