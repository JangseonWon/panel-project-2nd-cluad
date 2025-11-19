package com.gcgenome.lims.client.interfaces.api;

import com.gcgenome.lims.client.usecase.work.WorkRepository;
import com.gcgenome.lims.client.usecase.worklist.WorklistRepository;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public interface ApiModule {
    @Provides static FetchApi provideFetchApi() { return new FetchApi() {}; }
    @Binds WorklistRepository provideWorklistRepository(WorklistApi impl);
    @Binds WorkRepository provideWorkRepository(WorkApi impl);
}
