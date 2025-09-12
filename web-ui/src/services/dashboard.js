import api from './api';

export const getCountryDistribution = () => api.get('/dataView/countryDistribution', {params: {pageNumber: 1, pageSize: 7}});
export const getLanguageDistribution = () => api.get('/dataView/languageDistribution');
export const getContributorAnalysis = () => api.get('/dataView/contributorAnalysis');
export const getTimeSeriesAnalysis = () => api.get('/dataView/timeSeriesAnalysis');
export const getThematicSummary = () => api.get('/dataView/thematicSummary');
export const getCorpusOverview = () => api.get('/dataView/corpusOverview');
export const getCollegeOverview = () => api.get('/dataView/collegeOverview', {params: {pageNumber: 1, pageSize: 10}});
export const getDomainOverview = () => api.get('/dataView/domainOverview', {params: {pageNumber: 1, pageSize: 10}});
export const getRecentUploads = () => api.get('/dataView/recentUploads');
