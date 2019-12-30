import React, {createContext, useReducer} from 'react';

const initialState = {};
const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ( { children } ) => {
    const [state, dispatch] = useReducer((state, action) => {
        switch(action.type) {
            case 'login':
                if (action.user === null) {
                    localStorage.removeItem("user");
                    return initialState;
                } else {
                    localStorage.setItem("user", JSON.stringify(action.user));
                }
                return {user: action.user,
                        authenticated: true};
            default:
                throw new Error();
        }
    }, initialState);

    return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

export { store, StateProvider }