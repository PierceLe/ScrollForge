import { Fallback } from '@frontend/modules/fallback';
import { Layout } from '@frontend/modules/layout';
import { Suspense } from 'react';
import { Routes as ReactRouterRoutes, Route } from 'react-router-dom';
import { PAGE_LINKS } from './permissionLink';

/**
 * File-based routing.
 * @desc File-based routing that uses React Router under the hood.
 * To create a new route create a new .jsx file in `/pages` with a default export.
 *
 * Some examples:
 * * `/pages/index.jsx` matches `/`
 * * `/pages/blog/[id].jsx` matches `/blog/123`
 * * `/pages/[...catchAll].jsx` matches any URL not explicitly matched
 *
 * @param {object} pages value of import.meta.globEager(). See https://vitejs.dev/guide/features.html#glob-import
 *
 * @return {Routes} `<Routes/>` from React Router, with a `<Route/>` for each file in `pages`
 */
export const Routes = ({ pages }: { pages: Record<string, unknown> }) => {
  const routes = useRoutes(pages);
  const routeComponentsWithLayout: JSX.Element[] = [];
  const routeComponentsWithoutLayout: JSX.Element[] = [];
  const routesWithoutLayout = [PAGE_LINKS.LOGIN.path, PAGE_LINKS.REGISTER.path];
  console.log(routes);
  routes.forEach(({ path, component: Component }) => {
    if (routesWithoutLayout.includes(path)) {
      routeComponentsWithoutLayout.push(
        <Route key={path} path={path} element={<Component />} />,
      );
    } else {
      routeComponentsWithLayout.push(
        <Route
          key={path}
          path={path}
          element={
            <Layout>
              <Component />
            </Layout>
          }
        />,
      );
    }
  });

  const NotFound = routes?.find(
    ({ path }) => path === PAGE_LINKS.NOT_FOUND.path,
  )?.component;

  return (
    <Suspense fallback={<Fallback />}>
      <ReactRouterRoutes>
        {routeComponentsWithLayout}
        {routeComponentsWithoutLayout}
        <Route
          path="*"
          element={
            <Layout>
              <NotFound />
            </Layout>
          }
        />
      </ReactRouterRoutes>
    </Suspense>
  );
};

const useRoutes = (pages: Record<string, any>) => {
  const routes = Object.keys(pages)
    .map(key => {
      let path = key
        .replace('./pages', '')
        .replace(/\.(t|j)sx?$/, '')
        /**
         * Replace /index with /
         */
        .replace(/\/index$/i, '/')
        /**
         * Only lowercase the first letter. This allows the developer to use camelCase
         * dynamic paths while ensuring their standard routes are normalized to lowercase.
         */
        .replace(/\b[A-Z]/, firstLetter => firstLetter.toLowerCase())
        /**
         * Convert /[handle].jsx and /[...handle].jsx to /:handle.jsx for react-router-dom
         */
        .replace(/\[(?:[.]{3})?(\w+?)\]/g, (_match, param) => `:${param}`);

      if (path.endsWith('/') && path !== '/') {
        path = path.substring(0, path.length - 1);
      }

      return {
        path,
        component: pages[key],
      };
    })
    .filter(route => route.component);

  return routes;
};
