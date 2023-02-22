import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import LogicalLocationMySuffix from './logical-location-my-suffix';
import LogicalLocationMySuffixDetail from './logical-location-my-suffix-detail';
import LogicalLocationMySuffixUpdate from './logical-location-my-suffix-update';
import LogicalLocationMySuffixDeleteDialog from './logical-location-my-suffix-delete-dialog';

const LogicalLocationMySuffixRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<LogicalLocationMySuffix />} />
    <Route path="new" element={<LogicalLocationMySuffixUpdate />} />
    <Route path=":id">
      <Route index element={<LogicalLocationMySuffixDetail />} />
      <Route path="edit" element={<LogicalLocationMySuffixUpdate />} />
      <Route path="delete" element={<LogicalLocationMySuffixDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LogicalLocationMySuffixRoutes;
