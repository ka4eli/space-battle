function f() {
    alert("I'm here");
}

function intToHex(i) {
    var res = "";
    switch (i) {
        case 10:
            res = "A";
            break;
        case 11:
            res = "B";
            break;
        case 12:
            res = "C";
            break;
        case 13:
            res = "D";
            break;
        case 14:
            res = "E";
            break;
        case 15:
            res = "F";
            break;
        default:
            res = i;
    }
    return res;
}