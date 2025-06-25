import mongoose from 'mongoose';
import axios from 'axios';

const MONGO_URI = 'mongodb://root:root@mongo:27017/bdc?directConnection=true&authSource=bdc';
const API_ENDPOINT = 'https://provinces.open-api.vn/api?depth=3';

const ProvinceSchema = new mongoose.Schema({
    _id: Number,
    name: String
}, { timestamps: false, versionKey: false });

const DistrictSchema = new mongoose.Schema({
    _id: Number,
    name: String,
    provinceId: Number
}, { timestamps: false, versionKey: false });

const WardSchema = new mongoose.Schema({
    _id: String,
    name: String,
    districtId: Number
}, { timestamps: false, versionKey: false });

const Province = mongoose.model('Province', ProvinceSchema);
const District = mongoose.model('District', DistrictSchema);
const Ward = mongoose.model('Ward', WardSchema);

async function fetchDataFromApi(url) {
    try {
        const response = await axios.get(url);
        return response.data;
    } catch (error) {
        return null;
    }
}

async function processAndInsertData(data) {
    try {
        await mongoose.connect(MONGO_URI);

        await Province.deleteMany({});
        await District.deleteMany({});
        await Ward.deleteMany({});

        const provinces = [];
        const districts = [];
        const wards = [];

        data.forEach(province => {
            provinces.push({
                _id: province.code,
                name: province.name
            });

            if (province.districts && Array.isArray(province.districts)) {
                province.districts.forEach(district => {
                    districts.push({
                        _id: district.code,
                        name: district.name,
                        provinceId: province.code
                    });

                    if (district.wards && Array.isArray(district.wards)) {
                        district.wards.forEach(ward => {
                            wards.push({
                                _id: ward.code,
                                name: ward.name,
                                districtId: district.code
                            });
                        });
                    }
                });
            }
        });

        if (provinces.length > 0) {
            await Province.insertMany(provinces);
        }

        if (districts.length > 0) {
            await District.insertMany(districts);
        }

        if (wards.length > 0) {
            await Ward.insertMany(wards);
        }

        return true;
    } catch (error) {
        return false;
    } finally {
        await mongoose.disconnect();
    }
}

async function checkDatabaseConnection() {
    try {
        console.log('🔌 Đang kiểm tra kết nối database...');
        await mongoose.connect(MONGO_URI);
        console.log('✅ Kết nối database thành công!');
        await mongoose.disconnect();
        return true;
    } catch (error) {
        console.log('❌ Không thể kết nối database:', error.message);
        return false;
    }
}

async function waitForDatabase(maxAttempts = 30, delayMs = 2000) {
    console.log('⏳ Đợi database khởi động...');

    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
        console.log(`🔄 Thử kết nối lần ${attempt}/${maxAttempts}...`);

        if (await checkDatabaseConnection()) {
            console.log('🎉 Database đã sẵn sàng!');
            return true;
        }

        if (attempt < maxAttempts) {
            console.log(`⏳ Đợi ${delayMs/1000} giây trước khi thử lại...`);
            await new Promise(resolve => setTimeout(resolve, delayMs));
        }
    }

    console.log('❌ Không thể kết nối database sau nhiều lần thử.');
    return false;
}

async function runImporter() {
    // Đợi database khởi động
    const dbReady = await waitForDatabase();
    if (!dbReady) {
        console.log('❌ Dừng import do không thể kết nối database.');
        return;
    }

    console.log('🚀 Bắt đầu import dữ liệu...');

    const apiData = await fetchDataFromApi(API_ENDPOINT);

    if (apiData && apiData.length > 0) {
        const success = await processAndInsertData(apiData);
        if (success) {
            console.log("✅ Import thành công!");
        } else {
            console.log("❌ Import thất bại.");
        }
    } else {
        console.log("⚠️ Không có dữ liệu để import.");
    }
}

runImporter();