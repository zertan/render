import * as squint_core from 'squint-cljs/core.js';
var or = (function () {
 let f2 = (function (var_args) {
let G__67 = arguments["length"];
switch (G__67) {case 2:
return f2.cljs$core$IFn$_invoke$arity$2((arguments[0]), (arguments[1]));
break;
case 3:
return f2.cljs$core$IFn$_invoke$arity$3((arguments[0]), (arguments[1]), (arguments[2]));
break;
default:
let args_arr49 = [];
let len__24637__auto__10 = arguments["length"];
let i511 = 0;
while(true){
let test__26256__auto__12 = (i511 < len__24637__auto__10);
if (test__26256__auto__12 != null && test__26256__auto__12 !== false) {
args_arr49.push((arguments[i511]));
let G__13 = (i511 + 1);
i511 = G__13;
continue;
};break;
}
;
let argseq__24738__auto__14 = (function () {
 let test__26256__auto__15 = (3 < args_arr49["length"]);
if (test__26256__auto__15 != null && test__26256__auto__15 !== false) {
return args_arr49.slice(3);}
})();
return f2.cljs$core$IFn$_invoke$arity$variadic((arguments[0]), (arguments[1]), (arguments[2]), argseq__24738__auto__14);}
});
f2["cljs$core$IFn$_invoke$arity$2"] = (function (_AMPERSAND_form, _AMPERSAND_env) {
return null;
});
f2["cljs$core$IFn$_invoke$arity$3"] = (function (_AMPERSAND_form, _AMPERSAND_env, x) {
return x;
});
f2["cljs$core$IFn$_invoke$arity$variadic"] = (function (_AMPERSAND_form, _AMPERSAND_env, x, next) {
return sequence(squint_core.seq(squint_core.concat(squint_core.list(symbol("let")), squint_core.list(squint_core.vec(sequence(squint_core.seq(squint_core.concat(squint_core.list(symbol("or__1__auto__")), squint_core.list(x)))))), squint_core.list(sequence(squint_core.seq(squint_core.concat(squint_core.list(symbol("if")), squint_core.list(symbol("or__1__auto__")), squint_core.list(symbol("or__1__auto__")), squint_core.list(sequence(squint_core.seq(squint_core.concat(squint_core.list(symbol("or")), next)))))))))));
});
f2["cljs$lang$applyTo"] = (function (seq16) {
let G__1720 = squint_core.first(seq16);
let seq1621 = squint_core.next(seq16);
let G__1822 = squint_core.first(seq1621);
let seq1623 = squint_core.next(seq1621);
let G__1924 = squint_core.first(seq1623);
let seq1625 = squint_core.next(seq1623);
let self__24647__auto__26 = this;
return self__24647__auto__26.cljs$core$IFn$_invoke$arity$variadic(G__1720, G__1822, G__1924, seq1625);
});
f2["cljs$lang$maxFixedArity"] = 3;
return f2;
})()
;

export { or }
