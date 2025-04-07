module.exports = function (doc) {
    doc.categoryId = doc.category
    doc.shopId = doc.shop
    return _.pick(
        doc,
        '_id',
        'name',
        'thumbnailUrl',
        'categoryId',
        'shopId',
        'price',
        'location',
        'sold',
        'createdAt',
        'averageRating',
        'totalReviews',
        'visible',
        'deleted',
        'restricted'
    )
};