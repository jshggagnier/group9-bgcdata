// var inter = window.setInterval(greet, 1000);

// var a = 0;
// function greet(){
//     console.log("hello " + a);
//     a++;
//     if (a >= 10){
//         window.clearInterval(inter);
//     }
// }

// console.log("hello world!");

// var b = document.getElementById("but");
// b.addEventListener('click', alertMe);

// function alertMe() {
//     window.alert("Clicked!");
// }

document.getElementById("but").addEventListener('click', function(evt){
    window.alert("Clicked!");
})

window.addEventListener('keypress', function(evt){
    console.log("key pressed: " + evt.key);
})