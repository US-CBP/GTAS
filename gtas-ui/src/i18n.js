import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import detector from "i18next-browser-languagedetector";
import backend from "i18next-xhr-backend";

// see translation files at '../public/locales/[lang]/translation.json'

i18n
  .use(detector)
  .use(backend)
  .use(initReactI18next)
  .init({
    // lng: "fr",
    fallbackLng: "en",
    keySeparator: false,
    // debug: true,
    interpolation: { escapeValue: false }
  });

export default i18n;
