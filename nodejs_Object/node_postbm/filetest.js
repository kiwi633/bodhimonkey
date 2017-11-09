'use strict'

var fs = require('fs');

fs.readFile("./student.json", "UTF-8", function (err, data) {
    if (err) {
        console.log(err);
    } else {
        console.log(data);
        console.log("%s bytes", data.length);
        console.log(data.length, " bytes");
    }
});
console.log("hello student.xml");

try {
    var data = fs.readFileSync("./student1.xml", "UTF-8");
    console.log(data);
} catch (err) {
    console.error("同步读取文件出錯: ",err);
};

fs.writeFile("./writetest.txt","成魔还是成佛！！",function(err){
    if(err){
        console.log(err);
    }else{
        console.log("OK!");
    }
});

// 获取文件大小
fs.stat('./student.json',function(err,status){
    if(err){
        console.log("错误信息：",err);
    }else{
        console.log("文件大小：",status.size);
        console.log("是否是文件",status.isFile());
    }
});


