/**
 * generate a random integer number within the specified range
 * @param low number low bound
 * @param high number high bound
 * @param radix number radix, default is 10
 * @returns {number} generated number
 */
function randInt(low, high, radix) {
    radix = radix || 10;
    var lowDeci = parseInt(low, radix)
    var highDeci = parseInt(high, radix)
    var res = Math.floor(Math.random() * (highDeci - lowDeci + 1)) + lowDeci;
    return res;
}

/**
 * generate a double number within the specified range
 * @param low low bound
 * @param high high bound
 * @returns {*} generated double number
 */
function randDouble(low, high) {
    var range = high - low;
    var rand = Math.random();
    return low + range * rand;
}

/**
 * get a random element from the input array
 * @param arr input array
 * @returns {*} a random element in input array
 */
function randArr(arr) {
    var i = randInt(0,arr.length - 1);
    return arr[i];
}
