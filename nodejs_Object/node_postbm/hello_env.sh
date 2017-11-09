#!/usr/bin/env node
var name = process.argv[2];

var shell = require('shelljs');

shell.exec("echo name: " + name );