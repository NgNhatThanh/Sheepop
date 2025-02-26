export const ERROR_TYPE = {
    INFOMATION_MISSING: 'Something is missing...',
    PRODUCT_NOT_EXIST: 'Product doesn\'t exist',
    UNKNOWN_ERROR: 'Something wrong, try again later!',
    NOT_FOUND: 'Not found!'
}

export default function ErrorPage({errorType = ERROR_TYPE.UNKNOWN_ERROR}){
    return (
        <div className="flex items-center justify-center h-screen bg-gray-100">
            <div className="text-center bg-white p-10 rounded-2xl shadow-lg max-h-[60vh] flex flex-col justify-center">
                <h1 className="text-4xl font-bold text-red-500 mb-4">Oops!</h1>
                <p className="text-lg text-gray-700 mb-6">
                    {errorType}
                </p>
                <a
                    href="/"
                    className="bg-blue-500 text-white px-6 py-3 rounded-lg text-lg font-medium hover:bg-blue-600 transition"
                >
                Trở về Trang Chủ
                </a>
            </div>
        </div>
      );
}