import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

// Add JWT token to every request automatically
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// AUTH
export const login = (username, password) => {
    return axios.post(`${API_URL}/auth/login`, { username, password });
};

export const register = (name, surname, username, password) => {
    return axios.post(`${API_URL}/auth/registration`, { name, surname, username, password });
};

// ARTICLES
export const getArticles = () => {
    return axios.get(`${API_URL}/article`);
};

// SAVED ARTICLES
export const saveArticle = (articleId) => {
    return axios.post(`${API_URL}/saved-article`, { articleId });
};

export const getSavedArticles = () => {
    return axios.get(`${API_URL}/saved-article`);
};

export const removeSavedArticle = (articleId) => {
    return axios.delete(`${API_URL}/saved-article/${articleId}`);
};