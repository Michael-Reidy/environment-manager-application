import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EnvironmentMySuffix from './environment-my-suffix';
import LogicalLocationMySuffix from './logical-location-my-suffix';
import NamespaceMySuffix from './namespace-my-suffix';
import SettingMySuffix from './setting-my-suffix';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="environment-my-suffix/*" element={<EnvironmentMySuffix />} />
        <Route path="logical-location-my-suffix/*" element={<LogicalLocationMySuffix />} />
        <Route path="namespace-my-suffix/*" element={<NamespaceMySuffix />} />
        <Route path="setting-my-suffix/*" element={<SettingMySuffix />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
