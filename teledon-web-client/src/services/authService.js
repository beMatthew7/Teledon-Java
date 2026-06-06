import apiClient from './apiClient';

const authService = {
    async login(username, password) {
        const response = await apiClient.post('/auth/login', { username, password });
        return response.data;
    },
};

export default authService;
