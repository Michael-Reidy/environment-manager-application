import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EnvironmentMySuffix from './environment-my-suffix';
import EnvironmentMySuffixDetail from './environment-my-suffix-detail';
import EnvironmentMySuffixUpdate from './environment-my-suffix-update';
import EnvironmentMySuffixDeleteDialog from './environment-my-suffix-delete-dialog';

const EnvironmentMySuffixRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EnvironmentMySuffix />} />
    <Route path="new" element={<EnvironmentMySuffixUpdate />} />
    <Route path=":id">
      <Route index element={<EnvironmentMySuffixDetail />} />
      <Route path="edit" element={<EnvironmentMySuffixUpdate />} />
      <Route path="delete" element={<EnvironmentMySuffixDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EnvironmentMySuffixRoutes;
