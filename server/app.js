const Koa = require('koa');
const koaBody = require('koa-body');
var cors = require('koa2-cors');
const fs = require('fs');

const app = new Koa();

app.use(cors());
app.use(koaBody());

let i = 0;
app.use(ctx => {
  const body = `Request Body: ${JSON.stringify(ctx.request.body)}`;
  fs.writeFile('html/' + (i++) + ctx.request.body.url.replace(/[\/]/ig, '_')+ '.html', ctx.request.body.html, function(err){
    if (err) {
      console.error(err)
    }
    console.log(body);
  });
  ctx.body = '';
});

app.listen(3005);
