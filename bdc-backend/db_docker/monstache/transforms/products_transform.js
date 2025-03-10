module.exports = function (doc) {
    doc.categoryId = doc.category
    return _.pick(
        doc,
        '_id',
        'name',
        'thumbnailUrl',
        'categoryId',
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