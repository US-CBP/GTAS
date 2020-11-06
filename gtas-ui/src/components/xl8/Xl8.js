import { asArray } from "../../utils/utils";
import { useTranslation } from "react-i18next";
import "./Xl8.css";

/**
 * **Wrapper for I18Next that can translate nested components recursively**
 */
const Xl8 = props => {
  let kids = props.children;
  const [t, i18n] = useTranslation();

  // SetTranslation finds any child component that has an xid tag, and translate its text value with
  // i18next. Walk the tree. Need to guard against redundant calls here tho if we keep the recursion, #29.
  // Multiple parent components could wrap children in xl8, Need to break when a child is an Xl8, return child.
  // TODO - APB on that ^
  // Also needs perf testing. Will be slower than manually adding each xl8/trans tag.

  const setTranslation = kidOrig => {
    let kid = Object.assign({}, kidOrig);

    if (Array.isArray(kid)) {
      kid.forEach(element => {
        return setTranslation(element);
      });
    }

    if (kid.xid !== undefined || kid.xname !== undefined) {
      let updated = Object.assign({}, kid);
      const xField = updated.xname;

      // preserve the default text where translation not found, BUT mark the child when
      // in DEV so we can highlight it or otherwise indicate it is not translated!!
      // Except that weirdly, marking the className prop only updates the DOM *sometimes*.
      // See #27.

      // here the translation fxn t() will return the id passed if no translation is found.
      // This doesn't work for us because our IDs are numeric rather than semantic, and we don't want those
      // numbers displaying. So we test whether the translation returns the ID and if so, set the default text
      // back to the original field value.

      const attempt = t(kid.xid);

      const translatedText = attempt !== kid.xid ? attempt : kid[xField];

      const style = attempt !== kid.xid ? " xl8-success" : " xl8-notfound";

      updated.className = updated.className + style;
      updated[xField] = translatedText || kid[xField];

      return updated;
    }

    if (kid.children !== undefined) {
      kid.children = asArray(kid.children).map(elem => setTranslation(elem));
      return kid;
    }

    if (kid.props !== undefined) {
      kid.props = setTranslation(kid.props);
      return kid;
    }

    return kid;
  };

  // const xl8 = id => {
  //   return mapping.find(item => item.id === id.toString());
  // };

  // //mapping is mimicking what will become a collection of translation files made
  // // of KVPs. Any attempt to match on an ID not present will return the default text
  // const mapping = [
  //   { id: "1", xtext: "Mangues" },
  //   { id: "2", xtext: "Carotttes" },
  //   { id: "3", xtext: "Oingons" },
  //   { id: "4", xtext: "Vert" },
  //   { id: "5", xtext: "Jaune" },
  //   { id: "6", xtext: "Bleu" }
  // ];

  let translation = asArray(kids).map(function(kid) {
    if (kid.props.children) {
      return setTranslation(kid);
    }

    return kid;
  });

  return translation;
};

export default Xl8;
