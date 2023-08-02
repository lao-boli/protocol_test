function newGV(varName,initValue) {
    this[varName] = initValue || 0;
}
function delGV(varName) {
    delete this[varName]
}

function incGV(varName,increment) {
    this[varName] = this[varName] + increment
    return this[varName]
}

function decGV(varName,decrement) {
    this[varName] = this[varName] - decrement
    return this[varName]
}

