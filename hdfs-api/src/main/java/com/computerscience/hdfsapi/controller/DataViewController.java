package com.computerscience.hdfsapi.controller;

import com.computerscience.hdfsapi.model.*;
import com.computerscience.hdfsapi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dataView")
public class DataViewController {

    @Autowired
    private CorpusOverviewService corpusOverviewService;

    @Autowired
    private CountryDistributionService countryDistributionService;

    @Autowired
    private LanguageDistributionService languageDistributionService;

    @Autowired
    private ContributorAnalysisService contributorAnalysisService;

    @Autowired
    private TimeSeriesAnalysisService timeSeriesAnalysisService;

    @Autowired
    private ThematicSummaryService thematicSummaryService;


    @Autowired
    private CollegeOverviewService collegeOverviewService;

    @Autowired
    private DomainOverviewService domainOverviewService;

    @GetMapping("/corpusOverview")
    public List<CorpusOverview> getCorpusOverview() {
        return corpusOverviewService.list();
    }

    @GetMapping("/countryDistribution")
    public List<CountryDistribution> getCountryDistribution() {
        return countryDistributionService.list();
    }

    @GetMapping("/languageDistribution")
    public List<LanguageDistribution> getLanguageDistribution() {
        return languageDistributionService.list();
    }

    @GetMapping("/contributorAnalysis")
    public List<ContributorAnalysis> getContributorAnalysis() {
        return contributorAnalysisService.list();
    }

    @GetMapping("/timeSeriesAnalysis")
    public List<TimeSeriesAnalysis> getTimeSeriesAnalysis() {
        return timeSeriesAnalysisService.list();
    }

    @GetMapping("/thematicSummary")
    public List<ThematicSummary> getThematicSummary() {
        return thematicSummaryService.list();
    }

    @GetMapping("/collegeOverview")
    public List<CollegeOverview> getCollegeOverview() {
        return collegeOverviewService.list();
    }

    @GetMapping("/domainOverview")
    public List<DomainOverview> getDomainOverview() {
        return domainOverviewService.list();
    }
}