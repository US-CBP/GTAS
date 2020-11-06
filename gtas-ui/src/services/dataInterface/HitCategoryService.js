/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *  
 */

import { useEffect, useState } from 'react'
import { ruleCats } from "../serviceWrapper";

export const useFetchHitCategories = () => {

    const [hitCategories, setHitCategories] = useState(undefined);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setError(null);
            ruleCats
                .get()
                .then(response => {
                    if (response) {
                        setHitCategories(response);
                        setLoading(false);
                        return response;
                    } else {
                        setHitCategories(response);
                        setLoading(false);
                        return [];
                    }
                })
                .catch(reason => {
                    console.log(reason);
                    setLoading(false);
                    return [];
                });
    }, []);
    return { hitCategories, loading, error }
};