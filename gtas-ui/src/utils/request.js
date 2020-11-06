
  export const pingUrl = (url) => {
    return new Promise(resolve => {
      let img = new Image();
      img.src = `${url}/favicon.ico?${Date.now()}`;
      img.addEventListener('load', () => {
        resolve(true);
      });
      img.addEventListener('error', () => {
        resolve(false);
      });
    })
    .catch( () => false);
  }


