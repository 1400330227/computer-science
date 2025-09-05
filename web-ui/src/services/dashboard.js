import api from './api';

export const getCountryDistribution = () => api.get('/dataView/countryDistribution');
export const getLanguageDistribution = () => api.get('/dataView/languageDistribution');
export const getContributorAnalysis = () => api.get('/dataView/contributorAnalysis');
export const getTimeSeriesAnalysis = () => api.get('/dataView/timeSeriesAnalysis');
export const getThematicSummary = () => api.get('/dataView/thematicAnalysis');