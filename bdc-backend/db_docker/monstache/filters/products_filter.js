module.exports = function (doc) {
    return doc.visible && !doc.deleted && !doc.restricted && doc.quantity > 0; 
};