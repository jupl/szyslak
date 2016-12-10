var proc = require('child_process')
var electron = require('electron')
var path = require('path')

proc.spawn(electron, ['.'], {cwd: path.resolve('target')})
