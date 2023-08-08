/**
 * create a new globe variable,default value is 0.
 * @param varName globe variable name
 * @param initValue variable initial value
 */
function newGV(varName,initValue) {
    this[varName] = initValue || 0;
}

/**
 * delete a globe variable
 * @param varName globe variable name
 */
function delGV(varName) {
    delete this[varName]
}

/**
 * increase a globe variable
 * @param varName globe variable name
 * @param increment increase value
 * @returns {*} the variable
 */
function incGV(varName,increment) {
    this[varName] = this[varName] + increment
    return this[varName]
}


/**
 * decrease a globe variable
 * @param varName globe variable name
 * @param decrement decrease value
 * @returns {*} the variable
 */
function decGV(varName,decrement) {
    this[varName] = this[varName] - decrement
    return this[varName]
}

