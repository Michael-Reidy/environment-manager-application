import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SettingMySuffix from './setting-my-suffix';
import SettingMySuffixDetail from './setting-my-suffix-detail';
import SettingMySuffixUpdate from './setting-my-suffix-update';
import SettingMySuffixDeleteDialog from './setting-my-suffix-delete-dialog';

const SettingMySuffixRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SettingMySuffix />} />
    <Route path="new" element={<SettingMySuffixUpdate />} />
    <Route path=":id">
      <Route index element={<SettingMySuffixDetail />} />
      <Route path="edit" element={<SettingMySuffixUpdate />} />
      <Route path="delete" element={<SettingMySuffixDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SettingMySuffixRoutes;
