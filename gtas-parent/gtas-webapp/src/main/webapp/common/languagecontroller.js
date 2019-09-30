/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('LanguageController', ['$scope', '$translate', '$location', '$cookies', 'APP_CONSTANTS',
                function ($scope, $translate, $location, $cookies, APP_CONSTANTS) {
    $scope.changeLanguage = function (locale) {
        console.log('locale has changed! ', locale);
        if(($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY)!= undefined) && ($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY)!=$scope.locale)){
            locale = $scope.locale;
            $cookies.put(APP_CONSTANTS.LOCALE_COOKIE_KEY, locale);
            $cookies.put('NG_TRANSLATE_LANG_KEY', locale);
        }

        $translate.refresh();
        $translate.use(locale);
    };

    $scope.getLocaleFromCookie = function(){
        if(($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY)!= undefined) || ($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY)!= null)){
            $translate.refresh($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY));
            $translate.use($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY));
        }
    };

    $scope.getLocaleFromCookie();

    {
    // $scope.list = [
    //     {
    //         "name": "United States",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 33},
    //             {"name": "English", "id": "en", "countryId": 33}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Algeria",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 1},
    //             {"name": "English", "id": "en", "countryId": 1}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Bahrain",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 2},
    //             {"name": "English", "id": "en", "countryId": 2}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Egypt",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 3},
    //             {"name": "English", "id": "en", "countryId": 3}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Iraq",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 5},
    //             {"name": "English", "id": "en", "countryId": 5}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Kuwait",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 6},
    //             {"name": "English", "id": "en", "countryId": 6}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Lebanon",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 7},
    //             {"name": "English", "id": "en", "countryId": 7}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Libya",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 8},
    //             {"name": "English", "id": "en", "countryId": 8}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Morocco",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 9},
    //             {"name": "English", "id": "en", "countryId": 9}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Oman",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 10},
    //             {"name": "English", "id": "en", "countryId": 10}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Qatar",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 11},
    //             {"name": "English", "id": "en", "countryId": 11}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Saudi Arabia",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 12},
    //             {"name": "English", "id": "en", "countryId": 12}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Sudan",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 13},
    //             {"name": "English", "id": "en", "countryId": 13}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Syria",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 14},
    //             {"name": "English", "id": "en", "countryId": 14}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Tunisia",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 15},
    //             {"name": "English", "id": "en", "countryId": 15}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "United Arab Emirates",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 16},
    //             {"name": "English", "id": "en", "countryId": 16}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Yemen",
    //         "version": [
    //             {"name": "Arabic", "id": "ar", "countryId": 17},
    //             {"name": "English", "id": "en", "countryId": 17}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Hong Kong",
    //         "version": [
    //             {"name": "Chinese", "id": "zh", "countryId": 18},
    //             {"name": "English", "id": "en", "countryId": 18}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Taiwan",
    //         "version": [
    //             {"name": "Chinese", "id": "zh", "countryId": 19},
    //             {"name": "English", "id": "en", "countryId": 19}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Czech Republic",
    //         "version": [
    //             {"name": "Czech", "id": "cs", "countryId": 20},
    //             {"name": "English", "id": "en", "countryId": 20}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Denmark",
    //         "version": [
    //             {"name": "Danish", "id": "da", "countryId": 21},
    //             {"name": "English", "id": "en", "countryId": 21}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Belgium",
    //         "version": [
    //             {"name": "Dutch", "id": "nl", "countryId": 22},
    //             {"name": "English", "id": "en", "countryId": 22}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Netherlands",
    //         "version": [
    //             {"name": "Dutch", "id": "nl", "countryId": 23},
    //             {"name": "English", "id": "en", "countryId": 23}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Australia",
    //         "version": [
    //             {"name": "English", "id": "en", "countryId": 24}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Canada",
    //         "version": [
    //             {"name": "French", "id": "fr", "countryId": 25},
    //             {"name": "English", "id": "en", "countryId": 25}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "India",
    //         "version": [
    //             {"name": "Hindi", "id": "hi", "countryId": 26},
    //             {"name": "English", "id": "en", "countryId": 26}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Ireland",
    //         "version": [
    //             {"name": "Irish", "id": "ga", "countryId": 27}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Malta",
    //         "version": [
    //             {"name": "Maltese", "id": "mt", "countryId": 28},
    //             {"name": "English", "id": "en", "countryId": 28}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "New Zealand",
    //         "version": [
    //             {"name": "English", "id": "en", "countryId": 29}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Philippines",
    //         "version": [
    //             {"name": "English", "id": "en", "countryId": 30}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Singapore",
    //         "version": [
    //             {"name": "Chinese", "id": "zh", "countryId": 31},
    //             {"name": "English", "id": "en", "countryId": 31}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "South Africa",
    //         "version": [
    //             {"name": "English", "id": "en", "countryId": 32}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "France",
    //         "version": [
    //             {"name": "French", "id": "fr", "countryId": 34},
    //             {"name": "English", "id": "en", "countryId": 34}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Luxembourg",
    //         "version": [
    //             {"name": "French", "id": "fr", "countryId": 35},
    //             {"name": "English", "id": "en", "countryId": 35},
    //             {"name": "German", "id": "de", "countryId": 35}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Switzerland",
    //         "version": [
    //             {"name": "French", "id": "fr", "countryId": 36},
    //             {"name": "English", "id": "en", "countryId": 36},
    //             {"name": "German", "id": "de", "countryId": 36},
    //             {"name": "Italian", "id": "it", "countryId": 36}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Austria",
    //         "version": [
    //             {"name": "German", "id": "de", "countryId": 37},
    //             {"name": "English", "id": "en", "countryId": 37}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Germany",
    //         "version": [
    //             {"name": "German", "id": "de", "countryId": 38},
    //             {"name": "English", "id": "en", "countryId": 38}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Cyprus",
    //         "version": [
    //             {"name": "Greek", "id": "el", "countryId": 39},
    //             {"name": "English", "id": "en", "countryId": 39}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Greece",
    //         "version": [
    //             {"name": "Greek", "id": "el", "countryId": 40},
    //             {"name": "English", "id": "en", "countryId": 40}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Hungary",
    //         "version": [
    //             {"name": "Hungarian", "id": "hu", "countryId": 41},
    //             {"name": "English", "id": "en", "countryId": 41}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Iceland",
    //         "version": [
    //             {"name": "Icelandic", "id": "is", "countryId": 42},
    //             {"name": "English", "id": "en", "countryId": 42}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Indonesia",
    //         "version": [
    //             {"name": "Indonesian", "id": "in", "countryId": 43},
    //             {"name": "English", "id": "en", "countryId": 43}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Ireland",
    //         "version": [
    //             {"name": "Irish", "id": "ga", "countryId": 44},
    //             {"name": "English", "id": "en", "countryId": 44}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Italy",
    //         "version": [
    //             {"name": "Italian", "id": "it", "countryId": 45},
    //             {"name": "English", "id": "en", "countryId": 45}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Japan",
    //         "version": [
    //             {"name": "Japan", "id": "ja", "countryId": 46},
    //             {"name": "English", "id": "en", "countryId": 46}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "South Korea",
    //         "version": [
    //             {"name": "Korean", "id": "ko", "countryId": 47},
    //             {"name": "English", "id": "en", "countryId": 47}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Malaysia",
    //         "version": [
    //             {"name": "Malaysian", "id": "ms", "countryId": 48},
    //             {"name": "English", "id": "en", "countryId": 48}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Norway",
    //         "version": [
    //             {"name": "Norwegian", "id": "no", "countryId": 49},
    //             {"name": "English", "id": "en", "countryId": 49}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Poland",
    //         "version": [
    //             {"name": "Portuguese", "id": "pt", "countryId": 51},
    //             {"name": "English", "id": "en", "countryId": 51}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Portugal",
    //         "version": [
    //             {"name": "Portuguese", "id": "pt", "countryId": 52},
    //             {"name": "English", "id": "en", "countryId": 52}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Bosnia and Herzegovina",
    //         "version": [
    //             {"name": "Serbian", "id": "sr", "countryId": 53},
    //             {"name": "English", "id": "en", "countryId": 53}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Montenegro",
    //         "version": [
    //             {"name": "Serbian", "id": "sr", "countryId": 54},
    //             {"name": "English", "id": "en", "countryId": 54}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Serbia",
    //         "version": [
    //             {"name": "Serbian", "id": "sr", "countryId": 55},
    //             {"name": "English", "id": "en", "countryId": 55}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Slovakia",
    //         "version": [
    //             {"name": "Slovak", "id": "sk", "countryId": 56},
    //             {"name": "English", "id": "en", "countryId": 56}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Slovenia",
    //         "version": [
    //             {"name": "Slovenian", "id": "sl", "countryId": 57},
    //             {"name": "English", "id": "en", "countryId": 57}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Argentina",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 58},
    //             {"name": "English", "id": "en", "countryId": 58}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Bolivia",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 59},
    //             {"name": "English", "id": "en", "countryId": 59}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Chile",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 60},
    //             {"name": "English", "id": "en", "countryId": 60}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Colombia",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 61},
    //             {"name": "English", "id": "en", "countryId": 61}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Costa Rica",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 62},
    //             {"name": "English", "id": "en", "countryId": 62}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Dominican Republic",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 63},
    //             {"name": "English", "id": "en", "countryId": 63}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Ecuador",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 64},
    //             {"name": "English", "id": "en", "countryId": 64}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "The Savior",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 65},
    //             {"name": "English", "id": "en", "countryId": 65}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Guatemala",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 66},
    //             {"name": "English", "id": "en", "countryId": 66}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Honduras",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 67},
    //             {"name": "English", "id": "en", "countryId": 67}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Mexico",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 68},
    //             {"name": "English", "id": "en", "countryId": 68}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Nicaragua",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 69},
    //             {"name": "English", "id": "en", "countryId": 69}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Panama",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 70},
    //             {"name": "English", "id": "en", "countryId": 70}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Paraguay",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 71},
    //             {"name": "English", "id": "en", "countryId": 71}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Peru",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 72},
    //             {"name": "English", "id": "en", "countryId": 72}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Puerto Rico",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 73},
    //             {"name": "English", "id": "en", "countryId": 73}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Spain",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 74},
    //             {"name": "English", "id": "en", "countryId": 74}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Uruguay",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 75},
    //             {"name": "English", "id": "en", "countryId": 75}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Venezuela",
    //         "version": [
    //             {"name": "Spanish", "id": "es", "countryId": 76},
    //             {"name": "English", "id": "en", "countryId": 76}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Sweden",
    //         "version": [
    //             {"name": "Swedish", "id": "sv", "countryId": 77},
    //             {"name": "English", "id": "en", "countryId": 77}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Thailand",
    //         "version": [
    //             {"name": "Thai", "id": "th", "countryId": 78},
    //             {"name": "English", "id": "en", "countryId": 78}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Turkey",
    //         "version": [
    //             {"name": "Turkish", "id": "tr", "countryId": 79},
    //             {"name": "English", "id": "en", "countryId": 79}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Ukraine",
    //         "version": [
    //             {"name": "Ukrainian", "id": "uk", "countryId": 80},
    //             {"name": "English", "id": "en", "countryId": 80}
    //         ]
    //     }
    //     ,
    //     {
    //         "name": "Vietnam",
    //         "version": [
    //             {"name": "English", "id": "en", "countryId": 81},
    //             {"name": "Vietnamese", "id": "vi", "countryId": 81}
    //         ]
    //     }

    // ];

    // $scope.countries = [
    //     {"name": "United States", "id": 33},
    //     {"name": "Algeria", "id": 1},
    //     {"name": "Bahrain", "id": 2},
    //     {"name": "Egypt", "id": 3},
    //     {"name": "Iraq", "id": 4},
    //     {"name": "Jordan", "id": 5},
    //     {"name": "Kuwait", "id": 6},
    //     {"name": "Lebanon", "id": 7},
    //     {"name": "Libya", "id": 8},
    //     {"name": "Morocco", "id": 9},
    //     {"name": "Oman", "id": 10},
    //     {"name": "Qatar", "id": 11},
    //     {"name": "Saudi Arabia", "id": 12},
    //     {"name": "Sudan", "id": 13},
    //     {"name": "Syria", "id": 14},
    //     {"name": "Tunisia", "id": 15},
    //     {"name": "United Arab Emirates", "id": 16},
    //     {"name": "Yemen", "id": 17},
    //     {"name": "Hong Kong", "id": 18},
    //     {"name": "Taiwan", "id": 19},
    //     {"name": "Czech Republic", "id": 20},
    //     {"name": "Denmark", "id": 21},
    //     {"name": "Belgium", "id": 22},
    //     {"name": "Netherlands", "id": 23},
    //     {"name": "Australia", "id": 24},
    //     {"name": "Canada", "id": 25},
    //     {"name": "India", "id": 26},
    //     {"name": "Ireland", "id": 27},
    //     {"name": "Malta", "id": 28},
    //     {"name": "New Zealand", "id": 29},
    //     {"name": "Philippines", "id": 30},
    //     {"name": "Singapore", "id": 31},
    //     {"name": "South Africa", "id": 32},
    //     {"name": "France", "id": 34},
    //     {"name": "Luxembourg", "id": 35},
    //     {"name": "Switzerland", "id": 36},
    //     {"name": "Austria", "id": 37},
    //     {"name": "Germany", "id": 38},
    //     {"name": "Cyprus", "id": 39},
    //     {"name": "Greece", "id": 40},
    //     {"name": "Hungary", "id": 41},
    //     {"name": "Iceland", "id": 42},
    //     {"name": "Indonesia", "id": 43},
    //     {"name": "Ireland", "id": 44},
    //     {"name": "Italy", "id": 45},
    //     {"name": "Japan", "id": 46},
    //     {"name": "South Korea", "id": 47},
    //     {"name": "Malaysia", "id": 48},
    //     {"name": "Norway", "id": 49},
    //     {"name": "Poland", "id": 50},
    //     {"name": "Brazil", "id": 51},
    //     {"name": "Portugal", "id": 52},
    //     {"name": "Bosnia and Herzegovina", "id": 53},
    //     {"name": "Montenegro", "id": 54},
    //     {"name": "Serbia", "id": 55},
    //     {"name": "Slovakia", "id": 56},
    //     {"name": "Slovenia", "id": 57},
    //     {"name": "Argentina", "id": 58},
    //     {"name": "Bolivia", "id": 59},
    //     {"name": "Chile", "id": 60},
    //     {"name": "Colombia", "id": 61},
    //     {"name": "Costa Rica", "id": 62},
    //     {"name": "Dominican Republic", "id": 63},
    //     {"name": "Ecuador", "id": 64},
    //     {"name": "The Savior", "id": 65},
    //     {"name": "Guatemala", "id": 66},
    //     {"name": "Honduras", "id": 67},
    //     {"name": "Mexico", "id": 68},
    //     {"name": "Nicaragua", "id": 69},
    //     {"name": "Panama", "id": 70},
    //     {"name": "Paraguay", "id": 71},
    //     {"name": "Peru", "id": 72},
    //     {"name": "Puerto Rico", "id": 73},
    //     {"name": "Spain", "id": 74},
    //     {"name": "Uruguay", "id": 75},
    //     {"name": "Venezuela", "id": 76},
    //     {"name": "Sweden", "id": 77},
    //     {"name": "Thailand", "id": 78},
    //     {"name": "Turkey", "id": 79},
    //     {"name": "Ukraine", "id": 80},
    //     {"name": "Vietnam", "id": 81}
    // ];

    // $scope.locales = [
    //     {"name": "Arabic", "id": "ar", "countryId": 1},
    //     {"name": "English", "id": "en", "countryId": 1},
    //     {"name": "Arabic", "id": "ar", "countryId": 2},
    //     {"name": "English", "id": "en", "countryId": 2},
    //     {"name": "Arabic", "id": "ar", "countryId": 3},
    //     {"name": "English", "id": "en", "countryId": 3},
    //     {"name": "Arabic", "id": "ar", "countryId": 4},
    //     {"name": "English", "id": "en", "countryId": 4},
    //     {"name": "Arabic", "id": "ar", "countryId": 5},
    //     {"name": "English", "id": "en", "countryId": 5},
    //     {"name": "Arabic", "id": "ar", "countryId": 6},
    //     {"name": "English", "id": "en", "countryId": 6},
    //     {"name": "Arabic", "id": "ar", "countryId": 7},
    //     {"name": "English", "id": "en", "countryId": 7},
    //     {"name": "Arabic", "id": "ar", "countryId": 8},
    //     {"name": "English", "id": "en", "countryId": 8},
    //     {"name": "Arabic", "id": "ar", "countryId": 9},
    //     {"name": "English", "id": "en", "countryId": 9},
    //     {"name": "Arabic", "id": "ar", "countryId": 10},
    //     {"name": "English", "id": "en", "countryId": 10},
    //     {"name": "Arabic", "id": "ar", "countryId": 11},
    //     {"name": "English", "id": "en", "countryId": 11},
    //     {"name": "Arabic", "id": "ar", "countryId": 12},
    //     {"name": "English", "id": "en", "countryId": 12},
    //     {"name": "Arabic", "id": "ar", "countryId": 13},
    //     {"name": "English", "id": "en", "countryId": 13},
    //     {"name": "Arabic", "id": "ar", "countryId": 14},
    //     {"name": "English", "id": "en", "countryId": 14},
    //     {"name": "Arabic", "id": "ar", "countryId": 15},
    //     {"name": "English", "id": "en", "countryId": 15},
    //     {"name": "Arabic", "id": "ar", "countryId": 16},
    //     {"name": "English", "id": "en", "countryId": 16},
    //     {"name": "Arabic", "id": "ar", "countryId": 17},
    //     {"name": "English", "id": "en", "countryId": 17},
    //     {"name": "Chinese", "id": "zh", "countryId": 18},
    //     {"name": "English", "id": "en", "countryId": 18},
    //     {"name": "Chinese", "id": "zh", "countryId": 19},
    //     {"name": "English", "id": "en", "countryId": 19},
    //     {"name": "Czech", "id": "cs", "countryId": 20},
    //     {"name": "English", "id": "en", "countryId": 20},
    //     {"name": "Danish", "id": "da", "countryId": 21},
    //     {"name": "English", "id": "en", "countryId": 21},
    //     {"name": "Dutch", "id": "nl", "countryId": 22},
    //     {"name": "English", "id": "en", "countryId": 22},
    //     {"name": "Dutch", "id": "nl", "countryId": 23},
    //     {"name": "English", "id": "en", "countryId": 23},
    //     {"name": "English", "id": "en", "countryId": 24},
    //     {"name": "French", "id": "fr", "countryId": 25},
    //     {"name": "English", "id": "en", "countryId": 25},
    //     {"name": "Hindi", "id": "hi", "countryId": 26},
    //     {"name": "English", "id": "en", "countryId": 26},
    //     {"name": "Irish", "id": "ga", "countryId": 27},
    //     {"name": "English", "id": "en", "countryId": 27},
    //     {"name": "Maltese", "id": "mt", "countryId": 28},
    //     {"name": "English", "id": "en", "countryId": 28},
    //     {"name": "English", "id": "en", "countryId": 29},
    //     {"name": "English", "id": "en", "countryId": 30},
    //     {"name": "Chinese", "id": "zh", "countryId": 31},
    //     {"name": "English", "id": "en", "countryId": 31},
    //     {"name": "English", "id": "en", "countryId": 32},
    //     {"name": "Spanish", "id": "es", "countryId": 33},
    //     {"name": "English", "id": "en", "countryId": 33},
    //     {"name": "French", "id": "fr", "countryId": 34},
    //     {"name": "English", "id": "en", "countryId": 34},
    //     {"name": "French", "id": "fr", "countryId": 35},
    //     {"name": "English", "id": "en", "countryId": 35},
    //     {"name": "German", "id": "de", "countryId": 35},
    //     {"name": "French", "id": "fr", "countryId": 36},
    //     {"name": "English", "id": "en", "countryId": 36},
    //     {"name": "German", "id": "de", "countryId": 36},
    //     {"name": "Italian", "id": "it", "countryId": 36},
    //     {"name": "German", "id": "de", "countryId": 37},
    //     {"name": "English", "id": "en", "countryId": 37},
    //     {"name": "German", "id": "de", "countryId": 38},
    //     {"name": "English", "id": "en", "countryId": 38},
    //     {"name": "Greek", "id": "el", "countryId": 39},
    //     {"name": "English", "id": "en", "countryId": 39},
    //     {"name": "Greek", "id": "el", "countryId": 40},
    //     {"name": "English", "id": "en", "countryId": 40},
    //     {"name": "Hungarian", "id": "hu", "countryId": 41},
    //     {"name": "English", "id": "en", "countryId": 41},
    //     {"name": "Icelandic", "id": "is", "countryId": 42},
    //     {"name": "English", "id": "en", "countryId": 42},
    //     {"name": "Indonesian", "id": "in", "countryId": 43},
    //     {"name": "English", "id": "en", "countryId": 43},
    //     {"name": "Irish", "id": "ga", "countryId": 44},
    //     {"name": "English", "id": "en", "countryId": 44},
    //     {"name": "Italian", "id": "it", "countryId": 45},
    //     {"name": "English", "id": "en", "countryId": 45},
    //     {"name": "Japan", "id": "ja", "countryId": 46},
    //     {"name": "English", "id": "en", "countryId": 46},
    //     {"name": "Korean", "id": "ko", "countryId": 47},
    //     {"name": "English", "id": "en", "countryId": 47},
    //     {"name": "Malaysian", "id": "ms", "countryId": 48},
    //     {"name": "English", "id": "en", "countryId": 48},
    //     {"name": "Norwegian", "id": "no", "countryId": 49},
    //     {"name": "English", "id": "en", "countryId": 49},
    //     {"name": "Polish", "id": "po", "countryId": 50},
    //     {"name": "English", "id": "en", "countryId": 50},
    //     {"name": "Portuguese", "id": "pt", "countryId": 51},
    //     {"name": "English", "id": "en", "countryId": 51},
    //     {"name": "Portuguese", "id": "pt", "countryId": 52},
    //     {"name": "English", "id": "en", "countryId": 52},
    //     {"name": "Serbian", "id": "sr", "countryId": 53},
    //     {"name": "English", "id": "en", "countryId": 53},
    //     {"name": "Serbian", "id": "sr", "countryId": 54},
    //     {"name": "English", "id": "en", "countryId": 54},
    //     {"name": "Serbian", "id": "sr", "countryId": 55},
    //     {"name": "English", "id": "en", "countryId": 55},
    //     {"name": "Slovak", "id": "sk", "countryId": 56},
    //     {"name": "English", "id": "en", "countryId": 56},
    //     {"name": "Slovenian", "id": "sl", "countryId": 57},
    //     {"name": "English", "id": "en", "countryId": 57},
    //     {"name": "Spanish", "id": "es", "countryId": 58},
    //     {"name": "English", "id": "en", "countryId": 58},
    //     {"name": "Spanish", "id": "es", "countryId": 59},
    //     {"name": "English", "id": "en", "countryId": 59},
    //     {"name": "Spanish", "id": "es", "countryId": 60},
    //     {"name": "English", "id": "en", "countryId": 60},
    //     {"name": "Spanish", "id": "es", "countryId": 61},
    //     {"name": "English", "id": "en", "countryId": 61},
    //     {"name": "Spanish", "id": "es", "countryId": 62},
    //     {"name": "English", "id": "en", "countryId": 62},
    //     {"name": "Spanish", "id": "es", "countryId": 63},
    //     {"name": "English", "id": "en", "countryId": 63},
    //     {"name": "Spanish", "id": "es", "countryId": 64},
    //     {"name": "English", "id": "en", "countryId": 64},
    //     {"name": "Spanish", "id": "es", "countryId": 65},
    //     {"name": "English", "id": "en", "countryId": 65},
    //     {"name": "Spanish", "id": "es", "countryId": 66},
    //     {"name": "English", "id": "en", "countryId": 66},
    //     {"name": "Spanish", "id": "es", "countryId": 67},
    //     {"name": "English", "id": "en", "countryId": 67},
    //     {"name": "Spanish", "id": "es", "countryId": 68},
    //     {"name": "English", "id": "en", "countryId": 68},
    //     {"name": "Spanish", "id": "es", "countryId": 69},
    //     {"name": "English", "id": "en", "countryId": 69},
    //     {"name": "Spanish", "id": "es", "countryId": 70},
    //     {"name": "English", "id": "en", "countryId": 70},
    //     {"name": "Spanish", "id": "es", "countryId": 71},
    //     {"name": "English", "id": "en", "countryId": 71},
    //     {"name": "Spanish", "id": "es", "countryId": 72},
    //     {"name": "English", "id": "en", "countryId": 72},
    //     {"name": "Spanish", "id": "es", "countryId": 73},
    //     {"name": "English", "id": "en", "countryId": 73},
    //     {"name": "Spanish", "id": "es", "countryId": 74},
    //     {"name": "English", "id": "en", "countryId": 74},
    //     {"name": "Spanish", "id": "es", "countryId": 75},
    //     {"name": "English", "id": "en", "countryId": 75},
    //     {"name": "Spanish", "id": "es", "countryId": 76},
    //     {"name": "English", "id": "en", "countryId": 76},
    //     {"name": "Swedish", "id": "sv", "countryId": 77},
    //     {"name": "English", "id": "en", "countryId": 77},
    //     {"name": "Thai", "id": "th", "countryId": 78},
    //     {"name": "English", "id": "en", "countryId": 78},
    //     {"name": "Turkish", "id": "tr", "countryId": 79},
    //     {"name": "English", "id": "en", "countryId": 79},
    //     {"name": "Ukrainian", "id": "uk", "countryId": 80},
    //     {"name": "English", "id": "en", "countryId": 80},
    //     {"name": "English", "id": "en", "countryId": 81},
    //     {"name": "Vietnamese", "id": "vi", "countryId": 81}
    // ];

    // $scope.updateCountry = function () {
    //     $scope.availableLocales = [];

    //     angular.forEach($scope.locales, function (value) {
    //         if (value.countryId == $scope.country) {
    //             $scope.availableLocales.push(value);
    //         }
    //     });
    // }

    }
}]);
