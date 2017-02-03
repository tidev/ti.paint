// Copyright (c) 2017 Axway All Rights Reserved.
// Licensed under the terms of the Apache Public License.
// Please see the LICENSE included with this distribution for details.

var spawn = require('child_process').spawn,
    async = require('async'),
    path = require('path'),
    fs   = require('fs'),
    ejs  = require('ejs'),
    appc = require('node-appc');

exports.cliVersion = ">=3.2";
exports.init = function(logger, config, cli, nodeappc) {

    /*
     * CLI Hook for re-run cmake
     */
    cli.on('build.module.pre.compile', function (data, callback) {
        var tasks = [
            function(next) {
                runCmake(data, 'WindowsStore', 'Win32', '10.0', next);
            },
            function(next) {
                runCmake(data, 'WindowsStore', 'ARM', '10.0', next);
            },
            function(next) {
                runCmake(data, 'WindowsPhone', 'Win32', '8.1', next);
            },
            function(next) {
                runCmake(data, 'WindowsPhone', 'ARM', '8.1', next);
            },
            function(next) {
                runCmake(data, 'WindowsStore', 'Win32', '8.1', next);
            }
        ];

        async.series(tasks, function(err) {
            callback(err, data);
        });
    });
    
};

function runCmake(data, platform, arch, sdkVersion, next) {
    var logger = data.logger,
        generatorName = 'Visual Studio 14 2015' + (arch==='ARM' ? ' ARM' : ''),
        cmakeProjectName = (sdkVersion === '10.0' ? 'Windows10' : platform) + '.' + arch,
        cmakeWorkDir = path.resolve(__dirname,'..','..',cmakeProjectName);

    logger.debug('Run CMake on ' + cmakeWorkDir);

    if (!fs.existsSync(cmakeWorkDir)) {
        fs.mkdirSync(cmakeWorkDir);
    }

    var p = spawn(path.join(data.titaniumSdkPath,'windows','cli','vendor','cmake','bin','cmake.exe'),
        [
            '-G', generatorName,
            '-DCMAKE_SYSTEM_NAME=' + platform,
            '-DCMAKE_SYSTEM_VERSION=' + sdkVersion,
            '-DCMAKE_BUILD_TYPE=Debug',
            path.resolve(__dirname,'..','..')
        ],
        {
            cwd: cmakeWorkDir
        });
    p.on('error', function(err) {
        logger.error(cmake);
        logger.error(err);
    });
    p.stdout.on('data', function (data) {
        logger.info(data.toString().trim());
    });
    p.stderr.on('data', function (data) {
        logger.warn(data.toString().trim());
    });
    p.on('close', function (code) {
        if (code != 0) {
            process.exit(1); // Exit with code from cmake?
        }
        next();
    });
}
