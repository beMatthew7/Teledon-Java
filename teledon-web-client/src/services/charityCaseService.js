import apiClient from './apiClient';

const charityCaseService = {
    async getAll(maxAmount = null) {
        const config = maxAmount !== null ? { params: { maxAmount } } : undefined;
        const response = await apiClient.get('/charity-cases', config);
        return response.data;
    },

    async create(charityCase) {
        const response = await apiClient.post('/charity-cases', charityCase);
        return response.data;
    },

    async update(charityCase) {
        const response = await apiClient.put(`/charity-cases/${charityCase.id}`, charityCase);
        return response.data;
    },

    async delete(id) {
        const response = await apiClient.delete(`/charity-cases/${id}`);
        return response.data;
    },
};

export default charityCaseService;
