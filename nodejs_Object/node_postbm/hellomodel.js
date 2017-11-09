'use strict'

var s = 'Hello';

function greet(){
    return hello();
}

function hello(){
  console.log("hello");
}

function hello1(){
    console.log("hello1");
}

function hello2(name){
    console.log("hello2 "+name+"!");
}

module.exports = {
    hello:hello1,
    hello2:hello2
};