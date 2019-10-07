const http = require('http');
const fs = require('fs')
const path = require('path')
//create a server object:
const server = http.createServer(function (req, res) {
    const url = req.url;
    if (url === '/') {
        res.writeHead(200, { 'Content-Type': 'text/html' });
        res.write(`
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">
        <title>Document</title>
        </head>
        <body>
        
        <a href="/download">Akra Arşiv İndir</a>
        </body>
        </html>
        `);
        res.end();
    } else if (url === '/download') {
        res.setHeader('Content-Disposition', 'attachment; filename="AkraArsiv.apk"');
        const apkPath = path.resolve(__dirname, 'app-release.apk')
        const stream = fs.createReadStream(apkPath)
        stream.on('data', (data) => {

            res.write(data);
        })
        stream.on('end', () => {
            res.end();

        })
        stream.on('error', (err) => {
            res.end();

        })
        // res.write('<h1>contact us page<h1>'); //write a response
        // res.end(); //end the response
    }
})
const port = process.env.PORT || 3000;
server.listen(port, () => {
    console.log(`server start at port ${port}`); //the server object list
})