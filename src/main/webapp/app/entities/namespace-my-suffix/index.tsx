import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import NamespaceMySuffix from './namespace-my-suffix';
import NamespaceMySuffixDetail from './namespace-my-suffix-detail';
import NamespaceMySuffixUpdate from './namespace-my-suffix-update';
import NamespaceMySuffixDeleteDialog from './namespace-my-suffix-delete-dialog';

const NamespaceMySuffixRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<NamespaceMySuffix />} />
    <Route path="new" element={<NamespaceMySuffixUpdate />} />
    <Route path=":id">
      <Route index element={<NamespaceMySuffixDetail />} />
      <Route path="edit" element={<NamespaceMySuffixUpdate />} />
      <Route path="delete" element={<NamespaceMySuffixDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default NamespaceMySuffixRoutes;
