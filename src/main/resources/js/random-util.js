function randInt(low, high, radix) {
    radix = radix || 10;
    var lowDeci = parseInt(low, radix)
    var highDeci = parseInt(high, radix)
    var res = Math.floor(Math.random() * (highDeci - lowDeci + 1)) + lowDeci;
    return res;
}

function randDouble(low, high) {
    var range = high - low;
    var rand = Math.random();
    return low + range * rand;
}

function randArr(arr) {
    var i = randInt(0,arr.length - 1);
    return arr[i];
}
